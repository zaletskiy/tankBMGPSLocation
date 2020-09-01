'use strict';

let stompClient = null;

function connect() {
    let d = $.Deferred();

    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        d.resolve()
    });

    return d.promise();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function run() {
    stompClient.send("/app/run");
    stompClient.subscribe('/gps/liveData', function (messageOutput) {
        let $gpsData = $('#gpsData');
        $gpsData.show();
        $gpsData.append('<li class="list-group-item">' + messageOutput.body + '</li>');
    });

}


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

$("#connect").click(function () {
    connect().then(run);


});
$("#disconnect").click(function () {
    disconnect();
});


