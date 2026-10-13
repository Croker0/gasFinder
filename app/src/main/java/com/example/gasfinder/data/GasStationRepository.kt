package com.example.gasfinder.data

/**
 * Локальный (офлайн) справочник автозаправочных станций.
 *
 * Данные вымышленные и используются в учебном проекте: реальные названия улиц
 * Екатеринбурга и правдоподобные цены, но адреса и координаты сгенерированы.
 * Хранение справочника прямо в приложении даёт офлайн-режим и гарантирует,
 * что демонстрация не зависит от доступности внешних API.
 */
object GasStationRepository {

    /** Центр Екатеринбурга: используется как демо-локация. */
    const val DEMO_LATITUDE = 56.8389
    const val DEMO_LONGITUDE = 60.6057

    /** Базовый справочник АЗС (26 записей). */
    val stations: List<GasStation> = listOf(
        GasStation(
            id = 1,
            name = "АЗС №1 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Белинского, 152",
            latitude = 56.82513,
            longitude = 60.55203,
            prices = mapOf(
                FuelType.AI92 to 52.31,
                FuelType.AI95 to 57.07,
                FuelType.AI98 to 68.78,
                FuelType.DT to 62.39
            ),
            isOpen24 = true,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 2,
            name = "АЗС №2 «Neste»",
            brand = "Neste",
            address = "ул. Металлургов, 115",
            latitude = 56.83644,
            longitude = 60.64232,
            prices = mapOf(
                FuelType.AI92 to 58.87,
                FuelType.AI95 to 64.59,
                FuelType.AI98 to 77.23,
                FuelType.DT to 70.38
            ),
            isOpen24 = false,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 3,
            name = "АЗС №3 «Татнефть»",
            brand = "Татнефть",
            address = "ул. Гагарина, 29",
            latitude = 56.92236,
            longitude = 60.56915,
            prices = mapOf(
                FuelType.AI92 to 54.60,
                FuelType.AI95 to 58.01,
                FuelType.AI98 to 73.66,
                FuelType.DT to 65.68
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 4,
            name = "АЗС №4 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Чкалова, 70",
            latitude = 56.86816,
            longitude = 60.58891,
            prices = mapOf(
                FuelType.AI92 to 51.84,
                FuelType.AI95 to 56.25,
                FuelType.AI98 to 69.97,
                FuelType.DT to 61.68
            ),
            isOpen24 = true,
            hasCafe = false,
            hasShop = true,
            hasCarWash = true
        ),
        GasStation(
            id = 5,
            name = "АЗС №5 «Башнефть»",
            brand = "Башнефть",
            address = "ул. Уральская, 126",
            latitude = 56.87534,
            longitude = 60.53270,
            prices = mapOf(
                FuelType.AI92 to 52.51,
                FuelType.AI95 to 57.02,
                FuelType.AI98 to 67.06,
                FuelType.DT to 60.61
            ),
            isOpen24 = false,
            hasCafe = false,
            hasShop = true,
            hasCarWash = true
        ),
        GasStation(
            id = 6,
            name = "АЗС №6 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Свердлова, 96",
            latitude = 56.84953,
            longitude = 60.69492,
            prices = mapOf(
                FuelType.AI92 to 51.59,
                FuelType.AI95 to 55.73,
                FuelType.AI98 to 68.06,
                FuelType.DT to 63.58
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 7,
            name = "АЗС №7 «Башнефть»",
            brand = "Башнефть",
            address = "ул. Тургенева, 56",
            latitude = 56.74124,
            longitude = 60.58895,
            prices = mapOf(
                FuelType.AI92 to 50.59,
                FuelType.AI95 to 54.54,
                FuelType.AI98 to 69.63,
                FuelType.DT to 62.27
            ),
            isOpen24 = true,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 8,
            name = "АЗС №8 «Трасса»",
            brand = "Трасса",
            address = "ул. Чкалова, 77",
            latitude = 56.96511,
            longitude = 60.36241,
            prices = mapOf(
                FuelType.AI92 to 48.83,
                FuelType.AI95 to 54.81,
                FuelType.AI98 to 66.75,
                FuelType.DT to 58.26
            ),
            isOpen24 = false,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 9,
            name = "АЗС №9 «Neste»",
            brand = "Neste",
            address = "ул. Белинского, 102",
            latitude = 56.79982,
            longitude = 60.64869,
            prices = mapOf(
                FuelType.AI92 to 58.57,
                FuelType.AI95 to 62.67,
                FuelType.AI98 to 77.01,
                FuelType.DT to 69.49
            ),
            isOpen24 = false,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 10,
            name = "АЗС №10 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Ленина, 40",
            latitude = 56.87330,
            longitude = 60.57685,
            prices = mapOf(
                FuelType.AI92 to 53.40,
                FuelType.AI95 to 56.08,
                FuelType.AI98 to 68.64,
                FuelType.DT to 62.01
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 11,
            name = "АЗС №11 «Лукойл»",
            brand = "Лукойл",
            address = "ул. Краснолесья, 80",
            latitude = 56.86977,
            longitude = 60.60593,
            prices = mapOf(
                FuelType.AI92 to 55.17,
                FuelType.AI95 to 57.76,
                FuelType.AI98 to 70.61,
                FuelType.DT to 64.23
            ),
            isOpen24 = false,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 12,
            name = "АЗС №12 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Луначарского, 51",
            latitude = 56.90791,
            longitude = 60.67108,
            prices = mapOf(
                FuelType.AI92 to 51.87,
                FuelType.AI95 to 56.73,
                FuelType.AI98 to 70.29,
                FuelType.DT to 63.77
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 13,
            name = "АЗС №13 «Газпромнефть»",
            brand = "Газпромнефть",
            address = "ул. Академика Сахарова, 45",
            latitude = 56.82198,
            longitude = 60.48326,
            prices = mapOf(
                FuelType.AI92 to 57.95,
                FuelType.AI95 to 60.23,
                FuelType.AI98 to 74.80,
                FuelType.DT to 69.05
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 14,
            name = "АЗС №14 «Газпромнефть»",
            brand = "Газпромнефть",
            address = "ул. Щербакова, 172",
            latitude = 56.82320,
            longitude = 60.62001,
            prices = mapOf(
                FuelType.AI92 to 57.41,
                FuelType.AI95 to 60.25,
                FuelType.AI98 to 74.77,
                FuelType.DT to 66.83
            ),
            isOpen24 = false,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 15,
            name = "АЗС №15 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Тургенева, 104",
            latitude = 56.91193,
            longitude = 60.68682,
            prices = mapOf(
                FuelType.AI92 to 51.00,
                FuelType.AI95 to 55.85,
                FuelType.AI98 to 68.50,
                FuelType.DT to 64.12
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 16,
            name = "АЗС №16 «Газпромнефть»",
            brand = "Газпромнефть",
            address = "Сибирский тракт, 90",
            latitude = 56.77374,
            longitude = 60.52917,
            prices = mapOf(
                FuelType.AI92 to 57.95,
                FuelType.AI95 to 61.01,
                FuelType.AI98 to 74.54,
                FuelType.DT to 69.43
            ),
            isOpen24 = false,
            hasCafe = false,
            hasShop = false,
            hasCarWash = true
        ),
        GasStation(
            id = 17,
            name = "АЗС №17 «Татнефть»",
            brand = "Татнефть",
            address = "ул. Комсомольская, 171",
            latitude = 56.88143,
            longitude = 60.78481,
            prices = mapOf(
                FuelType.AI92 to 54.46,
                FuelType.AI95 to 59.34,
                FuelType.AI98 to 73.50,
                FuelType.DT to 66.99
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = true
        ),
        GasStation(
            id = 18,
            name = "АЗС №18 «Neste»",
            brand = "Neste",
            address = "ул. Московская, 64",
            latitude = 56.80797,
            longitude = 60.50806,
            prices = mapOf(
                FuelType.AI92 to 58.28,
                FuelType.AI95 to 62.00,
                FuelType.AI98 to 76.02,
                FuelType.DT to 70.58
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = false,
            hasCarWash = true
        ),
        GasStation(
            id = 19,
            name = "АЗС №19 «Татнефть»",
            brand = "Татнефть",
            address = "ул. Академика Сахарова, 17",
            latitude = 56.85337,
            longitude = 60.66560,
            prices = mapOf(
                FuelType.AI92 to 53.56,
                FuelType.AI95 to 59.19,
                FuelType.AI98 to 71.93,
                FuelType.DT to 66.88
            ),
            isOpen24 = true,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 20,
            name = "АЗС №20 «Лукойл»",
            brand = "Лукойл",
            address = "ул. Академика Сахарова, 71",
            latitude = 56.76012,
            longitude = 60.53251,
            prices = mapOf(
                FuelType.AI92 to 53.12,
                FuelType.AI95 to 57.35,
                FuelType.AI98 to 71.47,
                FuelType.DT to 64.33
            ),
            isOpen24 = false,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 21,
            name = "АЗС №21 «Лукойл»",
            brand = "Лукойл",
            address = "ул. Щербакова, 2",
            latitude = 56.81676,
            longitude = 60.67724,
            prices = mapOf(
                FuelType.AI92 to 53.88,
                FuelType.AI95 to 59.54,
                FuelType.AI98 to 72.77,
                FuelType.DT to 64.22
            ),
            isOpen24 = false,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 22,
            name = "АЗС №22 «Роснефть»",
            brand = "Роснефть",
            address = "ул. Металлургов, 38",
            latitude = 56.82672,
            longitude = 60.66867,
            prices = mapOf(
                FuelType.AI92 to 52.14,
                FuelType.AI95 to 55.62,
                FuelType.AI98 to 69.61,
                FuelType.DT to 64.15
            ),
            isOpen24 = false,
            hasCafe = true,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 23,
            name = "АЗС №23 «Neste»",
            brand = "Neste",
            address = "пр. Космонавтов, 116",
            latitude = 56.77901,
            longitude = 60.55505,
            prices = mapOf(
                FuelType.AI92 to 59.50,
                FuelType.AI95 to 61.71,
                FuelType.AI98 to 78.66,
                FuelType.DT to 71.20
            ),
            isOpen24 = false,
            hasCafe = false,
            hasShop = true,
            hasCarWash = false
        ),
        GasStation(
            id = 24,
            name = "АЗС №24 «Татнефть»",
            brand = "Татнефть",
            address = "ул. Академика Сахарова, 63",
            latitude = 56.87304,
            longitude = 60.63124,
            prices = mapOf(
                FuelType.AI92 to 55.56,
                FuelType.AI95 to 59.30,
                FuelType.AI98 to 72.60,
                FuelType.DT to 65.50
            ),
            isOpen24 = true,
            hasCafe = true,
            hasShop = true,
            hasCarWash = true
        ),
        GasStation(
            id = 25,
            name = "АЗС №25 «Башнефть»",
            brand = "Башнефть",
            address = "ул. Гагарина, 54",
            latitude = 56.86082,
            longitude = 60.65991,
            prices = mapOf(
                FuelType.AI92 to 52.80,
                FuelType.AI95 to 56.88,
                FuelType.AI98 to 69.22,
                FuelType.DT to 61.26
            ),
            isOpen24 = false,
            hasCafe = true,
            hasShop = false,
            hasCarWash = false
        ),
        GasStation(
            id = 26,
            name = "АЗС №26 «Газпромнефть»",
            brand = "Газпромнефть",
            address = "ул. Куйбышева, 160",
            latitude = 56.92021,
            longitude = 60.56633,
            prices = mapOf(
                FuelType.AI92 to 57.40,
                FuelType.AI95 to 60.32,
                FuelType.AI98 to 75.02,
                FuelType.DT to 69.16
            ),
            isOpen24 = true,
            hasCafe = false,
            hasShop = true,
            hasCarWash = true
        )
    )

    /** Все бренды из справочника, по алфавиту — для фильтра. */
    val brands: List<String> = stations.map { it.brand }.distinct().sorted()
}
