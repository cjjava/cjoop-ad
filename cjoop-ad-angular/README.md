### cjoop-ad-angularjs
# Installation

```sh
$ npm install cjoop-ad-angular
```

# usage

```sh
<!--可选,如果引入该文件,前三级数据不会访问后台服务-->
<script type="text/javascript" src="../dist/angular-ad-data.min.js" ></script>
<script type="text/javascript" src="../dist/angular-ad.min.js" ></script>

window.adhost='localhost:80';//默认配置,提供后台请求的rest服务地址.请参考cjoop-ad-rest模块.
html使用方式,通过bind-model属性配置多级选择:
  三级地址:{{province.name}} {{city.name}} {{county.name}}
  <ad bind-model="province,city,county"></ad>
  <br/>
  五级地址:{{province.name}} {{city.name}} {{county.id}} {{town.name}} {{village.name}}
  <ad bind-model="province,city,county,town,village"></ad>
  <br/>
  独立作用域:{{p.name}} {{ci.name}} {{co.name}} {{t.name}} {{v.name}}
  <ad bind-model="p,ci,co,t,v"></ad>
  <br/>
```
