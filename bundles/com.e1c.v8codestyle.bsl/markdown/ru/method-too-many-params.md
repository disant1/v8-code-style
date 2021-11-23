# Метод содержит слишком много параметров

Не рекомендуется объявлять в функциях много параметров (нужно ориентироваться на количество не более семи параметров), 
при этом не должно быть много параметров со значениями по умолчанию (нужно ориентироваться на количество не более трех таких параметров). 
В противном случае, читаемость вызывающего кода сильно снижается. 
Например, можно легко ошибиться в количестве запятых при передаче необязательных параметров.

## Неправильно

Например, неправильно:

```bsl
// Добавляет новое поле на форму, инициализирует его значениями по умолчанию.
Функция ДобавитьПолеФормы(ИмяПоля, Заголовок = Неопределено, ОбработчикПриИзменении = "", 
      ОбработчикНачалоВыбора = "", ШиринаПоля,
      ЦветФона = Неопределено, ЦветФонаЗаголовка = Неопределено, 
      Родитель = Неопределено, КартинкаШапки = Неопределено, ПутьКДанным = Неопределено,
      ТолькоПросмотрПоля = Ложь, СвязиПараметровВыбора = Неопределено)
…
КонецФункции

// вызывающий код
НовоеПоле = ДобавитьПолеФормы("СтараяЦена", НСтр("ru='Цена'"),,, 12, ЦветФона, ЦветЗаголовка, НоваяГруппа,,,Истина);
НовоеПоле.ЦветТекста = WebЦвета.Серый;
```

## Правильно

Правильно пересмотреть логику работы функций, оставив в ней только один ключевой параметр ИмяПоля:

```bsl
// Добавляет новое поле на форму, инициализирует его значениями по умолчанию.
Функция НовоеПолеФормы(ИмяПоля)  
…
КонецФункции

// вызывающий код
НовоеПоле = НовоеПолеФормы("СтараяЦена");
НовоеПоле.Заголовок  = НСтр("ru='Цена'");
НовоеПоле.ЦветФона   = ЦветФона;
НовоеПоле.ЦветТекста = WebЦвета.Серый;
НовоеПоле…. = …
…
```

## См.

- [Параметры процедур и функций](https://its.1c.ru/db/v8std#content:640:hdoc:5)