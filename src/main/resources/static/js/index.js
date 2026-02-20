import { GlobalWorkerOptions, getDocument } from "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/4.0.379/pdf.min.mjs";

const canvas = document.getElementById("pdf-canvas");
const errorMessage = document.getElementById("error-message");

// Указываем путь к вашему PDF-файлу
const pdfUrl = "https://mozilla.github.io/pdf.js/web/compressed.tracemonkey-pldi-09.pdf";  // <-- как указать этот путь


GlobalWorkerOptions.workerSrc = "https://cdnjs.cloudflare.com/ajax/libs/pdf.js/4.0.379/pdf.worker.min.mjs";

getDocument(pdfUrl)
    .promise
    .then(function (pdfDoc) {
        // Загружаем первую страницу
        return pdfDoc.getPage(1);
    })
    .then(function (page) {

        const scale = 0.5;
        const viewport = page.getViewport({ scale: scale });


        canvas.width = viewport.width;
        canvas.height = viewport.height;


        const ctx = canvas.getContext("2d");
        const renderContext = {
            canvasContext: ctx,
            viewport: viewport
        };

        return page.render(renderContext).promise;
    })
    .then(function () {
        console.log("PDF успешно отрендерен на canvas");
    })
    .catch(function (error) {
        console.error("Error loading PDF file:", error);
        errorMessage.textContent = "Ошибка загрузки PDF: файл не найден или повреждён.";
        errorMessage.style.display = "block";
    });