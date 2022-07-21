### Read me
Ok, Here is a Back-End project of my Web IDE coding sheep.

Btw, i hate java.





### TEMP API-DESIGH

#### Post: `/code`
emm, it is for running code.

it needs two <b>params</b>:
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
- `204`
    > means the codes you posted have some grammar problems so that it can't be compiled well.

##### msg
`msg` is corresponded to the `code`

- `Good!`--> `200`
- `Long time error...` --> `233`
- `Nop` --> `244`
##### res
the output of your run codes