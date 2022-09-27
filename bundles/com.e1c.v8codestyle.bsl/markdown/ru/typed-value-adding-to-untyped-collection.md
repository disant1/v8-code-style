# Добавление типизированного значения в не типизированную коллекцию

Проверяет, что вызов метода ```Добавить()``` происходит для типизированной коллекции.

## Неправильно

Тип элементов коллекции не указан.

```bsl
// @strict-types

Результат = Новый Массив();
	
Результат.Добавить(42);
```

## Правильно

Необходимо указать тип элементов коллекции.

```bsl
// @strict-types

Результат = Новый Массив(); // Массив из Число
	
Результат.Добавить(42);
```

## См.
