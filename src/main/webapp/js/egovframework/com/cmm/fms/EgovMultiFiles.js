/****************************************************************
 *
 * 파일명 : EgovMultiFiles.js
 * 설  명 : 업로드시 여러 파일에 대한 목록 표시 및 처리
 *
 * 수정일         수정자        수정내용
 * ----------   ---------   ----------------------------
 * 2019.10.15   취두영        다중 파일 업로드 개선 (HTML5)
 * 2019.12.06   신용호        EgovMultiFilesChecker 추가하여 확장자 체크 및 용량 체크 하기
 * 2020.08.07   신용호        수정시 첨부파일 개수 없는경우 예외수정, 확장자 체크 로직 보완
 *
 */
function MultiSelector(list_target, max, file_label) {

	// Where to write the list
	this.list_target = list_target;
	this.id = 0;
	// Is there a maximum?
	if (max) {
		this.max = max;
	} else {
		this.max = -1;
	}
	
	this.file_label = file_label;
	/**
	 * Add a new file input element
	 */
	_base = this;
	this.addElement = function(element) {
		// Make sure it's a file input element
		if (element.tagName == 'INPUT' && element.type == 'file') {

			// Add reference to this object
			element.multi_selector = this;

			// What to do when a file is selected
			element.onchange = function() {

				var i = 0;

				var sErrMsg = "첨부파일 개수는 [" + _base.max + "]까지 첨부할 수 있습니다!";
				var files_length = 1;
				// HTML5 지원여부 체크
				if (typeof element.files != "undefined") {
					files_length = element.files.length;
				}

				if (_base.max < files_length) {
					element.value = "";
					alert(sErrMsg);

					while (list_target.firstChild) {
						list_target.removeChild(list_target.firstChild);
					}

					return;
				}

				// 수정모드 체크
				if (document.getElementById("egov_file_view_table") != null) {
					var sum = files_length
							+ document.getElementById("egov_file_view_table")
									.getElementsByTagName("img").length;

					if (_base.max < sum) {
						element.value = "";
						alert(sErrMsg);
						return;
					}
				}

				if (files_length > 0) {
					while (list_target.firstChild) {
						list_target.removeChild(list_target.firstChild);
					}
				}

				// Update list
				for (i; i < files_length; i++) {
					this.multi_selector.addListRowNew(this, i);
				}

			};
			// Most recent element
			this.current_element = element;
		} else {
			// This can only be applied to file input elements!
			alert('Error: not a file input element');
		}
		;
	};

	/**
	 * Add a new row to the list of files
	 */
	this.addListRowNew = function(element, i) {

		var file = element.files[i];

		// Row div
		var new_row = document.createElement('div');
		new_row.className = "file_add_" + i;
		new_row.style.display = "inline-flex";
		new_row.style.alignItems = "center";
		new_row.style.gap = "6px";
		new_row.style.marginRight = "10px";
		new_row.style.marginBottom = "4px";

		new_row.appendChild(EgovFilePreview.createThumbnail(file));

		// 2026.07.13 KISA 보안취약점 조치: 선택한 파일명(element.files[i].name)에 HTML 태그가
		// 포함될 수 있어 innerHTML로 직접 대입하면 DOM XSS로 이어질 수 있으므로,
		// span 요소를 생성하고 textContent로 파일명을 안전하게 대입한다.
		var new_row_span = document.createElement('span');
		new_row_span.textContent = file.name;
		new_row.appendChild(new_row_span);
		new_row.appendChild(document.createTextNode('  '));

		// Add it to the list
		this.list_target.appendChild(new_row);
	};

};

/**
 * 파일을 고르면 바로 보여줄 작은 미리보기.
 * 이미지는 썸네일, PDF는 첫 페이지, 엑셀/워드/파워포인트는 내용 일부, ZIP은 안의 파일 목록,
 * 그 외 텍스트류는 내용 일부를 보여준다. 그래도 못 읽는 형식만 확장자 뱃지로 남는다.
 */
var EgovFilePreview = {
	PDF_JS_VERSION: '3.11.174',
	JSZIP_VERSION: '3.10.1',
	XLSX_VERSION: '0.18.5',
	pdfJsPromise: null,
	jsZipPromise: null,
	xlsxPromise: null,
	TEXT_EXTS: ['txt', 'csv', 'json', 'md', 'log', 'xml', 'js', 'css', 'html', 'yml', 'yaml', 'ini', 'properties', 'sql'],

	createThumbnail: function(file) {
		var ext = EgovMultiFilesChecker.getFileExtension(file.name).replace('.', '').toLowerCase();
		if (typeof FileReader === 'undefined' || typeof file.arrayBuffer !== 'function') {
			return this.badgeThumb(file.name);
		}
		if (file.type && file.type.indexOf('image/') === 0) {
			return this.imageThumb(file);
		}
		if (ext === 'pdf' || file.type === 'application/pdf') {
			return this.pdfThumb(file);
		}
		if (ext === 'xlsx' || ext === 'xls') {
			return this.sheetThumb(file);
		}
		if (ext === 'docx') {
			return this.officeXmlThumb(file, function(zip) {
				return zip.file('word/document.xml') ? 'word/document.xml' : null;
			});
		}
		if (ext === 'pptx') {
			return this.officeXmlThumb(file, function(zip) {
				var names = Object.keys(zip.files).filter(function(n) { return /^ppt\/slides\/slide\d+\.xml$/.test(n); }).sort();
				return names[0] || null;
			});
		}
		if (ext === 'zip') {
			return this.zipListThumb(file);
		}
		if (this.isTextLike(file, ext)) {
			return this.textThumb(file);
		}
		return this.badgeThumb(file.name);
	},

	isTextLike: function(file, ext) {
		if (file.type && file.type.indexOf('text/') === 0) return true;
		return this.TEXT_EXTS.indexOf(ext) !== -1;
	},

	box: function() {
		var el = document.createElement('div');
		el.style.width = "44px";
		el.style.height = "44px";
		el.style.borderRadius = "6px";
		el.style.border = "1px solid #d7dae0";
		el.style.flex = "none";
		el.style.overflow = "hidden";
		el.style.background = "#fff";
		return el;
	},

	styleTextBox: function(el) {
		el.style.padding = "3px";
		el.style.fontFamily = "monospace";
		el.style.fontSize = "6px";
		el.style.lineHeight = "1.2";
		el.style.color = "#444";
		el.style.whiteSpace = "pre-wrap";
		el.style.wordBreak = "break-all";
		el.style.background = "#fafbfc";
	},

	fillBadge: function(el, filename) {
		el.innerHTML = '';
		el.style.cssText = "";
		el.style.width = "44px";
		el.style.height = "44px";
		el.style.borderRadius = "6px";
		el.style.border = "1px solid #d7dae0";
		el.style.flex = "none";
		el.style.display = "flex";
		el.style.alignItems = "center";
		el.style.justifyContent = "center";
		el.style.background = "#f4f6fa";
		el.style.color = "#656b78";
		el.style.fontSize = "10px";
		el.style.fontWeight = "700";
		el.textContent = this.extLabel(filename);
	},

	imageThumb: function(file) {
		var img = document.createElement('img');
		img.alt = '';
		img.style.width = "44px";
		img.style.height = "44px";
		img.style.borderRadius = "6px";
		img.style.border = "1px solid #d7dae0";
		img.style.objectFit = "cover";
		img.style.flex = "none";
		var reader = new FileReader();
		reader.onload = function(e) { img.src = e.target.result; };
		reader.readAsDataURL(file);
		return img;
	},

	textThumb: function(file) {
		var el = this.box();
		this.styleTextBox(el);
		var reader = new FileReader();
		reader.onload = function(e) { el.textContent = String(e.target.result).slice(0, 180); };
		reader.readAsText(file.slice(0, 2000));
		return el;
	},

	/** 엑셀(.xlsx/.xls) - 첫 시트 앞부분을 CSV 형태로 읽어 보여준다. */
	sheetThumb: function(file) {
		var el = this.box();
		this.styleTextBox(el);
		var self = this;
		this.ensureXlsx().then(function(XLSX) {
			return file.arrayBuffer().then(function(buf) {
				var wb = XLSX.read(new Uint8Array(buf), { type: 'array', sheetRows: 6 });
				var firstSheet = wb.Sheets[wb.SheetNames[0]];
				el.textContent = XLSX.utils.sheet_to_csv(firstSheet).slice(0, 180) || '(빈 시트)';
			});
		}).catch(function() { self.fillBadge(el, file.name); });
		return el;
	},

	/** 워드(.docx)/파워포인트(.pptx) - zip 안의 본문 XML에서 글자만 뽑아 보여준다. */
	officeXmlThumb: function(file, resolvePath) {
		var el = this.box();
		this.styleTextBox(el);
		var self = this;
		this.ensureJSZip().then(function(JSZip) {
			return file.arrayBuffer().then(function(buf) { return JSZip.loadAsync(buf); });
		}).then(function(zip) {
			var path = resolvePath(zip);
			if (!path) { throw new Error('no entry'); }
			return zip.file(path).async('text');
		}).then(function(xml) {
			var text = xml.replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim();
			el.textContent = text.slice(0, 180) || '(내용 없음)';
		}).catch(function() { self.fillBadge(el, file.name); });
		return el;
	},

	/** ZIP - 안에 든 파일 이름 몇 개를 보여준다. */
	zipListThumb: function(file) {
		var el = this.box();
		this.styleTextBox(el);
		var self = this;
		this.ensureJSZip().then(function(JSZip) {
			return file.arrayBuffer().then(function(buf) { return JSZip.loadAsync(buf); });
		}).then(function(zip) {
			var names = Object.keys(zip.files).filter(function(n) { return !zip.files[n].dir; }).slice(0, 6);
			el.textContent = names.join('\n').slice(0, 180) || '(빈 압축파일)';
		}).catch(function() { self.fillBadge(el, file.name); });
		return el;
	},

	pdfThumb: function(file) {
		var el = this.box();
		el.style.display = "flex";
		el.style.alignItems = "center";
		el.style.justifyContent = "center";
		el.style.background = "#f4f6fa";
		var placeholder = document.createElement('span');
		placeholder.textContent = "PDF";
		placeholder.style.fontSize = "10px";
		placeholder.style.fontWeight = "700";
		placeholder.style.color = "#656b78";
		el.appendChild(placeholder);
		var self = this;

		// CDN(pdf.js)에서 PDF 첫 페이지를 그려본다. 인터넷이 없거나 렌더링이 실패하면 "PDF" 글자만 남는다.
		this.ensurePdfJs().then(function(pdfjsLib) {
			return file.arrayBuffer().then(function(buf) {
				return pdfjsLib.getDocument({ data: buf }).promise;
			}).then(function(pdf) {
				return pdf.getPage(1);
			}).then(function(page) {
				var viewport = page.getViewport({ scale: 1 });
				var scale = 44 / Math.max(viewport.width, viewport.height);
				var scaledViewport = page.getViewport({ scale: scale });
				var canvas = document.createElement('canvas');
				canvas.width = scaledViewport.width;
				canvas.height = scaledViewport.height;
				var ctx = canvas.getContext('2d');
				return page.render({ canvasContext: ctx, viewport: scaledViewport }).promise.then(function() {
					el.innerHTML = '';
					el.style.background = '#fff';
					el.appendChild(canvas);
				});
			});
		}).catch(function() { /* CDN 로드/렌더링 실패 시 PDF 글자만 남겨둔다 */ });

		return el;
	},

	badgeThumb: function(filename) {
		var el = this.box();
		this.fillBadge(el, filename);
		return el;
	},

	loadScript: function(src) {
		return new Promise(function(resolve, reject) {
			var script = document.createElement('script');
			script.src = src;
			script.onload = function() { resolve(); };
			script.onerror = reject;
			document.head.appendChild(script);
		});
	},

	ensurePdfJs: function() {
		if (window.pdfjsLib) return Promise.resolve(window.pdfjsLib);
		if (this.pdfJsPromise) return this.pdfJsPromise;
		var version = this.PDF_JS_VERSION;
		this.pdfJsPromise = this.loadScript('https://cdnjs.cloudflare.com/ajax/libs/pdf.js/' + version + '/pdf.min.js')
			.then(function() {
				window.pdfjsLib.GlobalWorkerOptions.workerSrc =
					'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/' + version + '/pdf.worker.min.js';
				return window.pdfjsLib;
			});
		return this.pdfJsPromise;
	},

	ensureJSZip: function() {
		if (window.JSZip) return Promise.resolve(window.JSZip);
		if (this.jsZipPromise) return this.jsZipPromise;
		this.jsZipPromise = this.loadScript('https://cdnjs.cloudflare.com/ajax/libs/jszip/' + this.JSZIP_VERSION + '/jszip.min.js')
			.then(function() { return window.JSZip; });
		return this.jsZipPromise;
	},

	ensureXlsx: function() {
		if (window.XLSX) return Promise.resolve(window.XLSX);
		if (this.xlsxPromise) return this.xlsxPromise;
		this.xlsxPromise = this.loadScript('https://cdnjs.cloudflare.com/ajax/libs/xlsx/' + this.XLSX_VERSION + '/xlsx.full.min.js')
			.then(function() { return window.XLSX; });
		return this.xlsxPromise;
	},

	extLabel: function(filename) {
		var ext = EgovMultiFilesChecker.getFileExtension(filename).replace('.', '').toUpperCase();
		return ext.length > 4 ? ext.slice(0, 4) : ext;
	}
};

var EgovMultiFilesChecker = {
	getFileExtension: function(filename) {

		if (filename == null) return "";
	    var __fileLen = filename.length;
	    var __lastDot = filename.lastIndexOf('.');
	    if (__lastDot < 0 ) return "";
	    var __fileExt = filename.substring(__lastDot, __fileLen).toLowerCase();
	 
	    return __fileExt;
	}
	// 결과가 true 인경우 허용
	// 결과가 false 인경우 불가 
	,checkExtensions: function(fileObjId, allowTypes) {
		if ( document.getElementById( fileObjId ) == null ) return false; // file객체가 없으면 승인하지 않는다.
		if ( typeof document.getElementById( fileObjId ).files == "undefined" )
			return this.checkExtensionsOldIE(fileObjId, allowTypes);
		else
			return this.checkExtensionsHTML5(fileObjId, allowTypes);
			
	}
	,checkExtensionsHTML5: function(fileObjId, allowTypes) {
		var __filelen = document.getElementById( fileObjId ).files.length;
		var __fileObjs = document.getElementById( fileObjId ).files;
	    if ( __filelen == 0 ) return true;
	    for(var i=0; i<__fileObjs.length; i++) {
	    	var __fileObj = __fileObjs[i];
	    	// 2026.03.23 kisa 보안취약점 조치 디버그 코드 삭제

	    	var __fileExt = this.getFileExtension(__fileObj.name);
	    	if ( __fileExt == "" || ("," + allowTypes + ",").indexOf("," + __fileExt + ",") < 0 ) {
	    		alert("허용되지 않는 확장자 입니다.["+__fileExt+"]");
	    		return false;
	    	}
	    }
	    
	    return true;
	}
	,checkExtensionsOldIE: function(fileObjId, allowTypes) {
		var __filelPath = document.getElementById( fileObjId ).value;
	    // 2026.03.23 kisa 보안취약점 조치 디버그 코드 삭제

	    var __fileExt = this.getFileExtension(__filelPath);
    	if ( __fileExt == "" || ("," + allowTypes + ",").indexOf("," + __fileExt + ",") < 0 ) {
    		alert("2.허용되지 않는 확장자 입니다.["+__fileExt+"]");
    		return false;
    	}
	    
	    return true;
	}

	// 결과가 true 인경우 허용
	// 결과가 false 인경우 불가
	,checkFileSize: function(fileObjId, allowSize) {
		if ( document.getElementById( fileObjId ) == null ) return false; // file객체가 없으면 승인하지 않는다.
		if ( typeof document.getElementById( fileObjId ).files == "undefined" )
			return this.checkFileSizeOldIE(fileObjId, allowSize);
		else
			return this.checkFileSizeHTML5(fileObjId, allowSize);
	}
	,checkFileSizeHTML5: function(fileObjId, allowSize) {
		var __filelen = document.getElementById( fileObjId ).files.length;
		var __fileObjs = document.getElementById( fileObjId ).files;
	    if ( __filelen == 0 ) return true;
	    for(var i=0; i<__fileObjs.length; i++) {
	    	var __fileObj = __fileObjs[i];
			// 2026.03.23 kisa 보안취약점 조치 디버그 코드 삭제
	    	
	    	if ( __fileObj.size > allowSize ) {
	    		alert("허용되지 않는 파일 사이즈 입니다.["+__fileObj.name+" : "+__fileObj.size+" bytes / "+allowSize+" bytes]");
	    		return false;
	    	}
	    }
	    
	    return true;
	}
	// 구형 IE 브라우저의 경우 사이즈 체크의 제한이 있습니다.
	,checkFileSizeOldIE: function(fileObjId, allowSize) {
		
		var __filelPath = document.getElementById( fileObjId ).value;
	    // 2026.03.23 kisa 보안취약점 조치 디버그 코드 삭제

    	alert("구형 브라우저에서는 파일 사이즈 체크를 할수 없습니다.");
	    
	    return true;
	}

};

