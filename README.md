# hireme-server

## Running

```bash
     $ mvn spring-boot:run
```

By default, it will run on port 5000. And use MySQL Database on `178.128.170.25:3306`.
You can change it in [application.properties](src/main/resources/application.properties) file

## API specification

### Authorization

`/api/auth`

| Method |    Path   | Request                                         | Response                 |
| ------ | :-------: | ----------------------------------------------- | ------------------------ |
| POST   | `/signin` | Body: <br>{usernameOrEmail, password}           | {accessToken, tokenType} |
| POST   | `/signup` | Body: <br>{fullname, username, email, password} | {success, message}       |

### User

`/api/user`, `/api/users`

| Method | Path                              | Request                      | Response                                                            |
| ------ | :-------------------------------- | ---------------------------- | ------------------------------------------------------------------- |
| GET    | `/user/me`                        |                              | {id, username, fullname}                                            |
| GET    | `/user/checkUsernameAvailability` | RequestParam: <br>{username} | {isAvailable}                                                       |
| GET    | `/user/checkEmailAvailability`    | RequestParam: <br>{email}    | {isAvailable}                                                       |
| GET    | `/users/{username}`               | PathVariable: <br>{username} | {id, username, fullname, createdAt, all_other_additional_user_info} |
