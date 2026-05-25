let currentInterval = null;
let fullInterval = null;

function loadCurrentAndNext() {
    $.get("/schedule/current", function(data) {

        const currentSection = $('#current-section');
        const nextTitle = $('#next-title');
        const currentBody = $('#currentTable tbody');
        const nextBody = $('#nextTable tbody');

        currentBody.empty();
        nextBody.empty();

        if (data.currentTrip) {
            // === Есть текущий поезд ===
            currentSection.show();
            nextTitle.text("Следующее расписание отбытия");

            const t = data.currentTrip;
            const phase = data.phase;

            let solarClass = (phase === "SOLAR") ? 'red' : 'green';
            let vitiClass  = (phase === "VITI")  ? 'red' : 'green';
            let pobedaClass = (phase === "POBEDA") ? 'red' : 'green';

            currentBody.html(`
                <tr>
                    <td class="${solarClass}">Солнечная</td>
                    <td class="${vitiClass}">Вити Черевичкина</td>
                    <td class="${pobedaClass}">Победа</td>
                </tr>
                <tr >
                    <td class="${solarClass}">${t.departure}</td>
                    <td class="${vitiClass}">${t.middle}</td>
                    <td class="${pobedaClass}">${t.arrival}</td>
                </tr>                
            `);
        }
        else if (data.dayIsOver) {
            // === Весь день закончился ===
            currentSection.hide();
            nextTitle.text("Расписание завтрашнего отбытия");
        }
        else {
            // === Перерыв между рейсами (но день ещё не закончился) ===
            currentSection.hide();
            nextTitle.text("Расписание cледующего отбытия");
        }

        // Заполняем следующие рейсы
        if (data.nextTrips && data.nextTrips.length > 0) {
            data.nextTrips.forEach(t => {
                nextBody.append(`
                    <tr>
                        <td>${t.departure}</td>
                        <td>${t.middle}</td>
                        <td>${t.arrival}</td>
                    </tr>
                `);
            });
        }
    });
}


function loadFullSchedule() {
    $.get("/schedule/full", function(data) {
        const tbody = $('#fullTable tbody');
        tbody.empty();

        const now = new Date();
        const currentTimeStr = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

        data.schedule.forEach((trip, index) => {
            const isCurrentTrip = (index === data.currentIndex);

            let rowClass = isCurrentTrip ? 'current-row' : '';
            let solarClass = '';
            let vitiClass = '';
            let pobedaClass = '';

            // Определяем, прошло ли время
            const depPast = trip.departure < currentTimeStr;
            const midPast = trip.middle < currentTimeStr;
            const arrPast = trip.arrival < currentTimeStr;

            if (isCurrentTrip && data.currentPhase) {
                const phase = data.currentPhase;
                solarClass = (phase === "SOLAR") ? 'red' : (depPast ? 'past' : '');
                vitiClass  = (phase === "VITI")  ? 'red' : (midPast ? 'past' : '');
                pobedaClass = (phase === "POBEDA") ? 'red' : (arrPast ? 'past' : '');
            } else {
                // Не текущий рейс
                solarClass = depPast ? 'past' : '';
                vitiClass  = midPast ? 'past' : '';
                pobedaClass = arrPast ? 'past' : '';
            }

            tbody.append(`
                <tr class="${rowClass}">
                    <td class="${solarClass}">${trip.departure}</td>
                    <td class="${vitiClass}">${trip.middle}</td>
                    <td class="${pobedaClass}">${trip.arrival}</td>
                </tr>
            `);
        });
    });
}

// ==================== ИНИЦИАЛИЗАЦИЯ ====================

$(document).ready(() => {
    // Запускаем обновление краткой версии
    loadCurrentAndNext();
    currentInterval = setInterval(loadCurrentAndNext, 15000); // 15 сек
});

// Переход в полную таблицу
$('#btnDetails').click(() => {
    $('#short-view').hide();
    $('#full-view').show();

    loadFullSchedule();

    // Запускаем автообновление полной таблицы
    if (fullInterval) clearInterval(fullInterval);
    fullInterval = setInterval(loadFullSchedule, 15000);
});

// Возврат в краткую версию
$('#btnBack').click(() => {
    $('#full-view').hide();
    $('#short-view').show();

    // Останавливаем обновление полной таблицы
    if (fullInterval) {
        clearInterval(fullInterval);
        fullInterval = null;
    }

    loadCurrentAndNext();
});

// Очистка интервалов при закрытии страницы
$(window).on('beforeunload', function() {
    if (currentInterval) clearInterval(currentInterval);
    if (fullInterval) clearInterval(fullInterval);
});


$(document).ready(function() {

    // Клик по станции
    $('.station-point').on('click', function() {
        const station = $(this).data('station');
        showStationModal(station);
    });

    // // Hover эффект для удобства
    // $('.station-point').hover(
    //     function() { $(this).attr('opacity', '0'); },
    //     function() { $(this).attr('opacity', '0'); }
    // );

});

// ====================== МОДАЛЬНОЕ ОКНО ======================
function showStationModal(stationKey) {
    $.get("/schedule/full", function(data) {

        let stationName = '';
        let times = [];

        switch(stationKey) {
            case 'solar':
                stationName = 'Солнечная';
                times = data.schedule.map(t => t.departure);
                break;
            case 'viti':
                stationName = 'Вити Черевичкина';
                times = data.schedule.map(t => t.middle);
                break;
            case 'pobeda':
                stationName = 'Победа';
                times = data.schedule.map(t => t.arrival);
                break;
        }

        const now = new Date();
        const currentTimeStr = `${now.getHours().toString().padStart(2,'0')}:${now.getMinutes().toString().padStart(2,'0')}`;

        let html = '<div class="times-list">';

        times.forEach((time, index) => {
            const isPast = time < currentTimeStr;
            const isCurrentTrip = (index === data.currentIndex);

            let itemClass = '';

            if (isCurrentTrip) {
                // Проверяем, проехал ли поезд уже эту станцию
                let stationPassed = false;

                if (stationKey === 'solar' && data.currentPhase !== "SOLAR") {
                    stationPassed = true;
                } else if (stationKey === 'viti' && (data.currentPhase === "POBEDA" || data.currentPhase === "NONE")) {
                    stationPassed = true;
                } else if (stationKey === 'pobeda' && data.currentPhase !== "POBEDA") {
                    stationPassed = true; // если фаза не POBEDA, значит уже проехал
                }

                if (stationPassed) {
                    itemClass = 'past';
                } else {
                    itemClass = 'current';
                }
            }
            else if (isPast) {
                itemClass = 'past';
            }
            else {
                itemClass = 'future';
            }

            html += `
                <div class="time-item ${itemClass}">
                    ${time}
                </div>
            `;
        });

        html += '</div>';

        $('#modalStationName').text(stationName);
        $('#modalSchedule').html(html);
        $('#stationModal').fadeIn(200);
    });
}

// ====================== ИНИЦИАЛИЗАЦИЯ ======================
$(document).ready(function() {

    $('.station-point').on('click', function() {
        const station = $(this).data('station');
        showStationModal(station);
    });

    // $('.station-point').hover(
    //     function() { $(this).attr('opacity', '0'); },
    //     function() { $(this).attr('opacity', '0'); }
    // );

    // Закрытие
    $('.close').on('click', () => $('#stationModal').fadeOut(200));

    $(document).on('click', '#stationModal', function(e) {
        if (e.target === this) $('#stationModal').fadeOut(200);
    });
});