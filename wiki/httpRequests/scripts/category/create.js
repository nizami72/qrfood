const nameAz = ["Toyuq", "Mal əti", "Şorba", "Düyü", "Salat", "Makaron", "Qarşiliq", "Qarışıq", "Toyuq", "Baliq", "Vegeteran", "Düyü", "Şorba", "Çörək zavodu", "Barma"];
const nameEn = ["Chicken", "Beef", "Soup", "Rice", "Salad", "Pasta", "Snack", "Mixed", "Chicken", "Fish", "Vegetarian", "Rice", "Soup", "Bakery", "Wrap"];
const nameRu = ["Курица", "Говядина", "Суп", "Рис", "Салат", "Паста", "Закуска", "Смешанная", "Курица", "Рыба", "Вегетарианская", "Рис", "Суп", "Пекарня", "Варенье"];
let x = Math.floor(Math.random() * nameEn.length);

const eateryIds = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29];
let eateryIdx = Math.floor(Math.random() * eateryIds.length);

client.global.set("nameAz", nameAz[x]);
client.global.set("nameEn", nameEn[x]);
client.global.set("nameRu", nameRu[x]);
client.global.set("eateryId", eateryIds[eateryIdx]);

