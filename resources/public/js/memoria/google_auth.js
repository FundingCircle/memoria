var scopes = 'https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.email';

function handleClientLoad() {
    gapi.client.setApiKey(apiKey);
    window.setTimeout(checkAuth,1);
}

function checkAuth() {
    gapi.auth.authorize({client_id: clientId, scope: scopes, immediate: true}, handleAuthResult);
}

function handleAuthResult(authResult) {
    if (authResult && !authResult.error) {
        makeApiCall();
    }
}

function handleAuthClick(event) {
    event.preventDefault();
    gapi.auth.authorize({client_id: clientId, scope: scopes, immediate: false}, handleAuthResult);
}

function makeApiCall() {
    gapi.client.load('plus', 'v1', function() {
        var request = gapi.client.plus.people.get({'userId': 'me'});

        request.execute(function(resp) {
            memoria.app.auth(resp);
        });
    });
}
