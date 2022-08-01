# В качестве правого операнда операции сравнения "ПОДОБНО" указано поле таблицы.

Правым операндом операции сравнения ПОДОБНО (LIKE) и СПЕЦСИМВОЛ (ESCAPE) может быть только литерал (параметр) или выражение над литералами. Шаблонными символами являются только «_» – любой символ и «%» – последовательность любых символов.

## Неправильно

Запрещено использовать в запросе поле таблицы (реквизит справочника) в качестве правого операнда операции сравнения "ПОДОБНО" и "СПЕЦСИМВОЛ".

```bsl
ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО Таблица.Поле1
```

## Правильно

При использовании в тексте запроса оператора ПОДОБНО и СПЕЦСИМВОЛ допустимо использовать только константные строковые литералы или параметры запроса.

```bsl
ВЫБРАТЬ
    Товары.Ссылка
ИЗ
    Справочник.Товары КАК Товары
ГДЕ
    Товары.СтранаПроисхождения.Наименование ПОДОБНО "123%!%" СПЕЦСИМВОЛ "!"
```

## См.

- [Общие требования к конфигурации.](https://its.1c.ru/db/v8std#content:467:hdoc)
- [Приложение 8. Особенности работы с различными СУБД.](http://its.1c.ru/db/v83doc#bookmark:dev:TI000001285)
- [Руководство разработчика Глава 8. Работа с запросами.](https://its.1c.ru/db/v8318doc#bookmark:dev:TI000000506)