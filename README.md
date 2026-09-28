# Spring Data JPA Task Manager

Продолжение REST Task Manager: хранение перенесено из ConcurrentHashMap в H2 через Spring Data JPA.

## Запуск

Запусти `TaskManagerApplication`.

REST:
http://localhost:8080/tasks

H2 Console:
http://localhost:8080/h2-console

Параметры H2:
- JDBC URL: jdbc:h2:mem:taskdb
- User Name: sa
- Password: пустой

После входа:
SELECT * FROM TASKS;

## Основные проверки

POST /tasks
{
  "title": "Купить продукты",
  "description": "После работы",
  "priority": "HIGH",
  "status": "NEW"
}

GET /tasks
GET /tasks/1
GET /tasks?status=NEW
GET /tasks?priority=HIGH
GET /tasks?status=NEW&priority=HIGH

GET /tasks/search?keyword=купить
GET /tasks/stats

GET /tasks?page=0&size=3&sort=id
GET /tasks?page=1&size=3&sort=id

PUT /tasks/1
PATCH /tasks/1/status
DELETE /tasks/1

## Что смотреть в IntelliJ

В консоли Hibernate будут видны SQL:
- create table
- insert
- select
- update
- delete

`ddl-auto=create-drop` означает, что база in-memory очищается после остановки приложения.
