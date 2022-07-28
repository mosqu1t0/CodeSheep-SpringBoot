## Read me
Ok, Here is a springboot project of my web ide.

Btw, i hate java.





## TEMP API-DESIGH

### Code Service

#### Post: `/code-run`
emm, it is for running code.

it needs <b>three params</b>:
- lang(language)
- code(just codes)
- input(the args of program)

this api <b>return</b> the results of run codes

example:
```json
{
  "code": 200,
  "msg": "Good!",
  "res": "xxx"
}
```
##### code
- `200`
    > means the codes you posted have been perfectly executed.
- `233`
    > means the codes you posted have been executed, but killed by script due to some reasons(eg: long loop)
- `244`
    > means the codes you posted have some grammar problems so that it can't be compiled well.
- `555`
    > dangerous things happen, holy shit, please contact me.

##### msg
`msg` is corresponded to the `code`

- `Good!`--> `200`
- `Long time error...` --> `233`
- `Nop` --> `244`
- `出问题了，快联系管理员` --> `555`
##### res
the output of your run codes

### User Service

#### Post: `/user`

Obviously, this api is for registering account.

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

##### code
- `200`
  > Register success but the account need to be activated.
- `400`
  > Register failure please contact me

##### msg
- `注册成功啦，快去邮箱激活帐号吧!` --> `200`
- `由于很神奇的原因，注册失败了` --> `400`

#### Post: `/user-login`
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

##### code
- `200`
  > login success.
- `244`
  > password error.
- `404`
  > account not found or account haven't activated.
- `405`
  > the account have exceptional errors

##### msg
- `登录成功!` --> `200`
- `密码错误...` --> `244`
- `账户不存在或者未激活` --> `404`
- `账户异常，请联系管理员处理` --> `405`

#### Get: /user

Activate account api.


it only needs <b>one param</b>:

example:
```url
/user?confirmCode=xxxxxx
```
