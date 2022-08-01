# Read me
Ok, Here is a springboot project of my web ide.

Btw, i hate java.





# TEMP API-DESIGH

## Code Service

### POST: `/code-run`
emm, it is for running code.

it needs <b>three params</b>:
- language(language)
- code(just codes)
- input(the args of program)

it <b>return</b> the results of running code.

example:
```json
{
  "code": 200,
  "msg": "Good!",
  "res": "xxx"
}
```
#### code
- `200`
    > means the codes you posted have been perfectly executed.
- `233`
    > means the codes you posted have been executed, but killed by script due to some reasons(eg: long loop)
- `244`
    > means the codes you posted have some grammar problems so that it can't be compiled well.
- `555`
    > dangerous things happen, holy shit, please contact me.

#### msg
`msg` is corresponded to the `code`

- `Good!`--> `200`
- `Long time error...` --> `233`
- `Nop` --> `244`
- `出问题了，快联系管理员` --> `555`
#### res
the output of your running code.

### POST: `/code`

it is used to save the code on database.

it needs <b>three params</b>:
- language(language)
- fileName(the filename)
- code(just codes)


and <b>one cookie</b>
- token: `xxx.xxx.xxx`




it <b>return</b> the results of save code

example:
```json
{
  "code": 200,
  "msg": "Good!"
}
```

#### code
- 200
  > success
- 300
  > name repeating
- 400
  > failure

#### msg
- `保存成功` --> `200`
- `文件已经存在了，换个名字吧` --> `300`
- `保存失败，请联系管理员` --> `400`

### DELETE: `/code`

delete a code file you have saved

needs <b>two params:</b>
- fileName
- language

needs <b>one cookie:</b>
- token: `xx.xxx.xx`

return the results of delete code.

<b>example:</b>

```json
{
  "code": 200,
  "msg": "删除成功！"
}
```

#### code
- 200
  > success
- 400
  > failure

#### msg
- `删除成功!` --> `200`
- `删除失败,请联系管理员` --> `400`


### GET: `/code`

get all codes of this account

needs <b>two params:</b>
- pageNum
  > which page list you want to get(default 1) 
- pageSize
  > how many items compose to one page(default 5)

needs <b>one cookie</b>
- token: `xx.xxx.xx`

return the list of codes and some page infos

<b>example:</b>

```json
{
  "total":11,
  "list":[
    {"language":"cpp","code":null,"input":null,"fileName":"hello","time":"2022-08-01T14:31:46"},
    {"language":"cpp","code":null,"input":null,"fileName":"good","time":"2022-07-30T15:23:00"}
  ],
  "pageNum":3,
  "pageSize":5,
  "size":1,
  "startRow":11,
  "endRow":11,
  "pages":3,
  "prePage":2,
  "nextPage":0,
  "isFirstPage":false,
  "isLastPage":true,
  "hasPreviousPage":true,
  "hasNextPage":false,
  "navigatePages":8,
  "navigatepageNums":[1, 2, 3],
  "navigateFirstPage":1,
  "navigateLastPage":3
}
```

## User Service

### POST: `/user`

Obviously, this is for registering account.

it needs <b>two params</b>:
- email
- password

<b>return</b> the results of creating account.

example:
```json
{
  "code": 200,
  "msg": "注册成功啦，快去邮箱激活帐号吧!"
}
```

#### code
- `200`
  > Register success but the account need to be activated.
- `244`
  > the email has been registered.
- `400`
  > Register fail, please contact me

#### msg
- `注册成功啦，快去邮箱激活帐号吧!` --> `200`
- `该账户已经注册过了喔，如果未激活请到邮箱激活` --> `244`
- `由于很神奇的原因，注册失败了` --> `400`

### POST: `/user-login`
Emm, login api

it needs <b>two params</b>:
- email
- password
- remember

<b>return</b> the results of login account.

example:
```json
{
  "code": 200,
  "msg": "登录成功!"
}
```

#### code
- `200`
  > login success.
- `244`
  > password error.
- `404`
  > account not found or account haven't activated.
- `405`
  > the account have exceptional errors

#### msg
- `登录成功!` --> `200`
- `密码错误...` --> `244`
- `账户不存在或者未激活` --> `404`
- `账户异常，请联系管理员处理` --> `405`

### Get: /user

Activate account api.


needs <b>one param</b>:

example:
```url
/user?confirmCode=xxxxxx
```
