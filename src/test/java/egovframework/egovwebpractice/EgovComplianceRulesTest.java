package egovframework.egovwebpractice;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/**
 * 전자정부 표준프레임워크 호환성 가이드(2024.11) 규칙을 빌드에서 자동 검증한다.
 * 규칙을 어기는 코드를 추가하면 mvn package 가 실패한다.
 */
@AnalyzeClasses(packages = "egovframework.egovwebpractice", importOptions = ImportOption.DoNotIncludeTests.class)
class EgovComplianceRulesTest {

    /** 규칙 3: MVC - Controller 는 DAO(@Repository, EgovAbstractMapper)를 직접 호출할 수 없다. */
    @ArchTest
    static final ArchRule controller_must_not_call_dao =
            noClasses().that().areAnnotatedWith(Controller.class).or().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().areAnnotatedWith(Repository.class)
                    .orShould().dependOnClassesThat().areAssignableTo(EgovAbstractMapper.class);

    /** 규칙 3: MVC - Controller 는 MyBatis 등 데이터 접근 API 를 직접 사용할 수 없다. */
    @ArchTest
    static final ArchRule controller_must_not_use_data_access_api =
            noClasses().that().areAnnotatedWith(Controller.class).or().areAnnotatedWith(RestController.class)
                    .should().dependOnClassesThat().resideInAnyPackage("org.apache.ibatis..", "org.mybatis..", "javax.sql..");

    /** 규칙 4: 서비스 - EgovAbstractServiceImpl 을 확장한다. */
    @ArchTest
    static final ArchRule service_must_extend_egov_abstract_service_impl =
            classes().that().areAnnotatedWith(Service.class)
                    .should().beAssignableTo(EgovAbstractServiceImpl.class);

    /** 규칙 4: 서비스 - 서비스별 개별 인터페이스를 구현한다. */
    @ArchTest
    static final ArchRule service_must_implement_service_interface =
            classes().that().areAnnotatedWith(Service.class)
                    .should(new ArchCondition<JavaClass>("implement a service interface in a '..service' package") {
                        @Override
                        public void check(JavaClass item, ConditionEvents events) {
                            boolean implemented = item.getRawInterfaces().stream()
                                    .anyMatch(i -> i.getPackageName().endsWith(".service"));
                            if (!implemented) {
                                events.add(SimpleConditionEvent.violated(item,
                                        item.getName() + " 은(는) '..service' 패키지의 서비스 인터페이스를 구현하지 않습니다."));
                            }
                        }
                    });

    /** 규칙 5: 데이터 액세스 - MyBatis DAO 는 EgovAbstractMapper 를 상속한다. */
    @ArchTest
    static final ArchRule dao_must_extend_egov_abstract_mapper =
            classes().that().areAnnotatedWith(Repository.class)
                    .should().beAssignableTo(EgovAbstractMapper.class);

    /** 규칙 6: 실행환경 확장 - 실행환경 클래스를 상속한 클래스는 org.egovframe.rte 패키지에 둘 수 없다. */
    @ArchTest
    static final ArchRule extended_classes_must_not_be_in_rte_package =
            noClasses().that().areAssignableTo(EgovAbstractServiceImpl.class)
                    .or().areAssignableTo(EgovAbstractMapper.class)
                    .should().resideInAPackage("org.egovframe.rte..");

    /** 규칙 6: 실행환경 확장 - 상속한 클래스 이름은 Egov 로 시작할 수 없다. */
    @ArchTest
    static final ArchRule extended_classes_must_not_start_with_egov =
            noClasses().that().areAssignableTo(EgovAbstractServiceImpl.class)
                    .or().areAssignableTo(EgovAbstractMapper.class)
                    .should().haveSimpleNameStartingWith("Egov");

    /** 규칙 7: 명명 규칙 통일 - 계층별 접미사. */
    @ArchTest
    static final ArchRule naming_convention =
            classes().that().areAnnotatedWith(Controller.class).should().haveSimpleNameEndingWith("Controller")
                    .andShould().resideInAPackage("..web..");

    @ArchTest
    static final ArchRule service_naming =
            classes().that().areAnnotatedWith(Service.class).should().haveSimpleNameEndingWith("ServiceImpl")
                    .andShould().resideInAPackage("..service.impl..");

    @ArchTest
    static final ArchRule dao_naming =
            classes().that().areAnnotatedWith(Repository.class).should().haveSimpleNameEndingWith("DAO")
                    .andShould().resideInAPackage("..service.impl..");
}
