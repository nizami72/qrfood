package az.qrfood.backend.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class FakeDataGenerator {


    private static final Random rand = new Random();
    private static final List<String> fakeAddresses = List.of(
            "0979 Deandre Prairie, Gradyland, IN 65361",
            "191 Lorenzo Springs, Elizabethville, MA 98759",
            "216 Douglas Common, North Omerbury, OK 41037",
            "294 Harris Alley, South Jimmystad, AK 71912",
            "3666 Brakus Bridge, Jeanettaside, ND 17150",
            "37577 Margorie Springs, New Britany, SD 66719",
            "40438 Ian Unions, Bechtelarfort, NV 10779",
            "573 Goodwin Squares, North Sabina, LA 34842",
            "65767 Gislason Vista, Ornfort, SC 65421",
            "805 Labadie Valley, New Norrisbury, IL 06637",
            "Apt. 233 2172 Santiago Throughway, West Carley, NC 54312",
            "Apt. 309 94199 Runte Lake, South Orenberg, AZ 83081",
            "Apt. 439 118 Quigley Brooks, North Millytown, NV 54603",
            "Apt. 794 711 Bettye Stravenue, Langport, GA 54712",
            "Apt. 889 13052 Isa Creek, New Margariteville, KY 78190",
            "Suite 119 6281 Baumbach Vista, Larkinmouth, LA 81678",
            "Suite 151 0734 Kuhlman Row, Maximinafort, AL 69406",
            "Suite 203 004 Herzog Views, South Roberto, UT 80945",
            "Suite 346 0629 DuBuque Stravenue, Carsonborough, NE 69903",
            "Suite 997 411 Hammes Loop, New Takisha, GA 01249"
            );

    private static final List<String> fakeEateryNames = List.of(
            "The Hungry Spoon",
            "Crimson Fork",
            "Golden Bite",
            "Urban Skillet",
            "The Rustic Table",
            "Spice Symphony",
            "Fork & Flame",
            "Velvet Diner",
            "The Roasted Olive",
            "Amber Grill",
            "Maple & Thyme",
            "The Whistling Kettle",
            "Sizzle & Smoke",
            "Cedar & Salt",
            "The Laughing Tomato",
            "PepperTree Bistro",
            "The Cozy Pan",
            "Savory Lane",
            "Flameberry Kitchen",
            "Bloom & Bite"
    );

    private static final List<String> fakeUsers = List.of(
            "Liam Smith",
            "Emma Johnson",
            "Noah Brown",
            "Olivia Davis",
            "Elijah Miller",
            "Ava Wilson",
            "James Moore",
            "Sophia Taylor",
            "Benjamin Anderson",
            "Isabella Thomas",
            "Lucas Jackson",
            "Mia White",
            "Henry Harris",
            "Amelia Martin",
            "Alexander Thompson",
            "Charlotte Garcia",
            "Daniel Martinez",
            "Harper Robinson",
            "Matthew Clark",
            "Evelyn Lewis"
        );

    public static int getRandomInt(int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    public static String generateFakeAddress() {
        return fakeAddresses.get(getRandomInt(0, fakeAddresses.size()));
    }

    public static String eateryName() {
        return fakeEateryNames.get(getRandomInt(0, fakeEateryNames.size()));
    }

    public static int numberOfTables() {
        return getRandomInt(1, 4);
    }

    public static double geo1() {
        return gD(40.0000, 40.9999);
    }

    public static double geo2() {
        return gD(49.0000, 49.9999);
    }

    public static List<String> phones() {
        int amount = getRandomInt(1, 4);
        List<String> phones = new ArrayList<>(amount);
        IntStream.range(0, amount).forEach(i -> {
            phones.add(fakePhoneNumbers.get(getRandomInt(0, fakePhoneNumbers.size())));
        });
        return phones;
    }

    /**
     * Generate fake user first and last name.

     * @param arg reduces selection od user to 4 means only fist arg amount might be returned.
     * @return First Last name
     */
    public static String user(int arg) {
        return fakeUsers.get(getRandomInt(0,arg));
    }

    public static String mail(String arg) {
        return arg.replace(" ", "") + "@qrfood.az";
    }



    private static double gD(double min, double max ) {
        double minq = 10.0001;
        double maxq = 10.9999;

        return new BigDecimal(ThreadLocalRandom.current().
                nextDouble(min, max)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    private static final List<String> fakePhoneNumbers = List.of(
            "99450 1234567",
           "(994) 55 7654321",
           "(994) 70 1122334",
           "(994) 12 9988776",
           "(994) 50 3456789",
           "(994) 55 4567890",
           "(994) 70 5678901",
           "(994) 12 6789012",
           "(994) 50 7890123",
           "(994) 55 8901234",
           "(994) 70 9012345",
           "(994) 12 1239876",
           "(994) 50 2341987",
           "(994) 55 3452098",
           "(994) 70 4563109",
           "(994) 12 5674210",
           "(994) 50 6785321",
           "(994) 55 7896432",
           "(994) 70 8907543",
           "(994) 12 9018654",
           "(994) 50 1129765",
           "(994) 55 2230876",
           "(994) 70 3341987",
           "(994) 12 4452098",
           "(994) 50 5563109"
    );

}