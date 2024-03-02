const randomId = () => Math.random().toString(36).slice(7);
const defaultSettings = {
    id: randomId(),
};
let settings = {};
const server = 'https://bigdeck.pics';

async function loadSettings() {
    return chrome.storage.local.get(defaultSettings).then(data => {
        settings = {
            ...defaultSettings,
            ...data,
        };

        document.querySelector('#id').value = settings.id;
    });
}

async function saveSettings() {
    settings = {
        ...settings,
        id: document.querySelector('#id').value,
    };
    return chrome.storage.local.set(settings)
        .then(updateUrl);
}

function updateUrl() {
    document.querySelector('#card-url').href = `${server}/card/${settings.id}?layer-name=Scryfall&layer-width=745&layer-height=1040`
}

document.addEventListener('DOMContentLoaded', function(e) {
        loadSettings()
            .then(saveSettings);
    });
document.getElementById('submit').onclick = (e) => {
    e.preventDefault();
    saveSettings();
}
document.querySelector('#show-hide-password').onclick = (e) => {
    const target = document.querySelector('#id');
    const toggle = document.querySelector('#show-hide-password');
    if (target.type == 'password') {
        target.type = 'string';
        toggle.textContent = 'Hide';
    } else {
        target.type = 'password';
        toggle.textContent = 'Show';
    }
}
document.querySelector('#randomize-id').onclick = (e) => {
    document.querySelector('#id').value = randomId();
    saveSettings();
}
