## 1.6.0 (2017-01-03)
* Add change from AndroidPdfViewer 2.4.0 which replaces RenderingAsyncTask with Handler to simplify code and work with testing frameworks

## 1.5.1 (2016-12-06)
* Fix bug with scrolling in horizontal mode

## 1.5.0 (2016-11-19)
* Add mechanism for providing documents from different sources - more info in README
* Update PdfiumAndroid to 1.5.0
* Thanks to document sources and PdfiumAndroid update, in-memory documents are supported
* Replace DragPinchListener with Android's GestureDetector to add support for fling gesture while zooming

## 1.4.1 (2016-11-15)
* Merge pull request by [skarempudi](https://github.com/skarempudi) to improve sample app
* Fix loading document from subfolder in assets directory

## 1.4.0 (2016-07-25)
* Fix NPE and IndexOutOfBound bugs when rendering parts
* Merge pull request by [paulo-sato-daitan](https://github.com/paulo-sato-daitan) for disabling page change animation
* Merge pull request by [Miha-x64](https://github.com/Miha-x64) for drawing background if set on `PDFView`

## 1.3.0 (2016-07-13)
* update PdfiumAndroid to 1.4.0 with support for rendering annotations
* merge pull request by [usef](https://github.com/usef) for rendering annotations

## 1.2.0 (2016-07-11)
* update PdfiumAndroid to 1.3.1 with support for bookmarks, Table Of Contents and documents with password:
  * added method `PDFView#getDocumentMeta()`, which returns document metadata
  * added method `PDFView#getTableOfContents()`, which returns whole tree of bookmarks in PDF document
  * added method `Configurator#password(String)`
* added horizontal mode to **ScrollBar** - use `ScrollBar#setHorizontal(true)` or `app:sb_horizontal="true"` in XML
* block interaction with `PDFView` when document is not loaded - prevent some exceptions
* fix `PDFView` exceptions in layout preview (edit mode)

## 1.1.2 (2016-06-27)
* update PdfiumAndroid to 1.1.0, which fixes displaying multiple `PDFView`s at the same time and few errors with loading PDF documents.

## 1.1.1 (2016-06-17)
* fixes bug with strange behavior when indices passed to `.pages()` don't start with `0`.

## 1.1.0 (2016-06-16)
* added method `pdfView.fromUri(Uri)` for opening files from content providers
* updated PdfiumAndroid to 1.0.3, which should fix bug with exception
* updated sample with demonstration of `fromUri()` method
* some minor fixes

## 1.0.0 (2016-06-06)
* Initial release
