import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 로컬(내장) PostgreSQL 을 조회·수정하는 작은 도구. 앱이 실행 중일 때 사용한다.
 *
 * 포트는 실행할 때마다 바뀐다. 앱 로그에서 "postmaster started ... on port N" 의 N 을 확인한다.
 *
 * 조회: java -cp <postgresql.jar> tools/DbQuery.java jdbc:postgresql://localhost:N/postgres "select * from comtnemplyrinfo"
 * 수정: java -cp <postgresql.jar> tools/DbQuery.java jdbc:postgresql://localhost:N/postgres --update "update ..."
 * postgresql.jar 는 보통 ~/.m2/repository/org/postgresql/postgresql/ 아래에 있다.
 * 로컬 개발 DB 전용이다(비밀번호 없이 postgres 사용자로 접속). 운영 DB 에는 쓰지 않는다.
 */
public class DbQuery {

    public static void main(String[] args) throws Exception {
        boolean update = args.length > 2 && "--update".equals(args[1]);
        String sql = update ? args[2] : args[1];
        try (Connection connection = DriverManager.getConnection(args[0], "postgres", "");
             Statement statement = connection.createStatement()) {
            if (update) {
                System.out.println("변경된 행: " + statement.executeUpdate(sql));
                return;
            }
            try (ResultSet rs = statement.executeQuery(sql)) {
                int columns = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columns; i++) {
                        row.append(rs.getString(i)).append(" | ");
                    }
                    System.out.println(row);
                }
            }
        }
    }
}
