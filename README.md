<h2>Приложение анализирует CSV файлы находящиеся в папке ./src/main/resources и формирует чек по запросу</h2>
<div>Пример опций при запуске приложения: <code>1-5 10-6 3-7 balanceDebitCard=100 discountCardNumber=1234</code> 
<br>
(5 единиц товара с ид 1, 6 единиц с ид 10, 7 единиц с ид 3 при этом имеющийся баланс - 100, номер скидочной карты - 1234)
</div>
<h3>Применены паттерны: </h3>
<ol>
    <li>Builder (рекорды Product, DiscountCard)</li>
    <li>Factory Method (интерфейс и производные от SingletonContextFactory)</li>
    <li>Strategy (класс SingletonContextFactoryImpl принимает в конструкторе Consumer-а, 
у которого затем вызывает accept в методе createContext)</li>
    <li>Application Context (интерфейс и производные от SingletonContext; хранит, в единственном экземпляре,
готовые для пользования объекты необходимых приложению интерфейсов/классов)</li>
    <li>DTO</li>
</ol>
<h3>Инструкция по запуску:</h3>
<ol>
    <li>Переходим в корневую директорию проекта</li>
<br>
    <li>Компилируем все необходимые для запуска файлы и помещаем в папку ./src : <br> <code>javac -d ./src ./src/main/java/ru/clevertec/check/*</code></li>
<br>
    <li>Запускаем приложение : <br> <code>java -cp ./src ./src/main/java/ru/clevertec/check/CheckRunner.java &lt;your options></code>
        <br>
        ИЛИ
        <br>
        <code>java -cp ./src ru.clevertec.check.CheckRunner &lt;your options></code>
    </li>
</ol>