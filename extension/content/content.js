const defaultSettings = {
    id: null,
};
let settings = {};
const server = 'https://bigdeck.pics';

function loadSettings() {
    chrome.storage.local.get(defaultSettings).then(data => {
        settings = {
            ...defaultSettings,
            ...data,
        };
    });
}

function changeCardImage(url) {
    console.log(`Change Card Image: ${url}`);
    if (settings.id === undefined || settings.id == null || settings.id.trim() == '') {
        alert('No ID configured for MTG Card Viewer. Use the extension settings page to set an ID.')
        throw new Error('No ID configured for MTG Card Viewer.');
    }

    fetch(`${server}/card/${settings.id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            id: settings.id,
            url: url,
        }),
    });
}

function injectCardButton(element, url) {
    const button = document.createElement('button');
    button.type = 'button';
    button.classList = 'button-n mcv-stream-button';
    button.textContent = 'Stream';
    button.onclick = (e) => {
        e.preventDefault();
        changeCardImage(url);
    };

    element.insertAdjacentElement('beforebegin', button);
}

loadSettings();

const targetImages = document.querySelectorAll(`img[src*="cards.scryfall.io/"]`);

targetImages.forEach(element => {
    let imageUrl = element.src;

    if (element.attributes['data-src']) {
        imageUrl = element.attributes['data-src'].value;
    }

    injectCardButton(element, imageUrl);
});
