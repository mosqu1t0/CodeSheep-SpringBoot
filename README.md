### Read me
Ok, Here is a springboot project of my web ide.

Btw, i hate java.





### TEMP API-DESIGH

#### Post: `/code`
emm, it is for running code.

it needs three <b>params</b>:
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
    > dangerous thing happen holy shit, please contact me.

##### msg
`msg` is corresponded to the `code`

- `Good!`--> `200`
- `Long time error...` --> `233`
- `Nop` --> `244`
##### res
the output of your run codes