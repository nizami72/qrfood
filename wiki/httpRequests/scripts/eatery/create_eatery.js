const names = ["Sunny Bistro", "Urban Kebab", "Cozy Corner", "Rice House", "Nova Star", "James Bond", "Qull Caffe"];
const addresses = ["123 Main Street, Baku", "45 Nizami St, Baku", "9 Azerbaijan Ave", "Old City, Baku"];
const phonePool = [
    ["+994501112233", "+994502223344"],
    ["+994504445566"],
    ["+994509998877", "+994505556677"],
    ["+994503334455"]
];
const categoryPool = [
    ["KEBAB", "SOUP"],
    ["RICE", "KEBAB"],
    ["SOUP", "RICE", "KEBAB"],
    ["KEBAB"]
];

// Вспомогательная функция для выбора случайного элемента
const getRandom = arr => arr[Math.floor(Math.random() * arr.length)];

client.global.set("name", getRandom(names));
client.global.set("address", getRandom(addresses));
client.global.set("phones", JSON.stringify(getRandom(phonePool)));
client.global.set("categories", JSON.stringify(getRandom(categoryPool)));
client.global.set("tablesAmount", Math.floor(Math.random() * 20) + 1);
client.global.set("geoLat", (Math.random() * (40.99999 - 40.00001) + 40.00001).toFixed(6));
client.global.set("geoLng", (Math.random() * (49.99999 - 49.00001) + 49.00001).toFixed(6));
