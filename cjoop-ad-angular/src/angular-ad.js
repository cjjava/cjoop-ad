/**
 * @license cjoop-ad v0.0.1
 * (c) 2010-2015 cjoop, Inc. http://www.cjoop.com
 * License: MIT
 */
(function(window, angular) {
	'use strict';
	/**
	 * 通过adhost设置远程rest访问地址
	 */
	function getADHost(){
		return window.adhost || "localhost:80";
	}
	/**
	 * 是否存在本地模式
	 */
	function isLocal(){
		return angular.isArray(window.adData) && window.adData.length>0;
	}
	angular.module('cjoop.ad',[])
		.directive('ad',['$http','$timeout',function($http,$timeout){
			return {
				scope:{},
				template:function($element,$attrs){
					var bindModel = $attrs.bindModel;
					if(!bindModel){
						throw new Error('The bind-model must not be null');
					} 
					var ngModels = bindModel.split(',');
					var template = new Array(ngModels.length);
					template.push('<div class="ad-wrapper">');
					angular.forEach(ngModels,function(item,index){
						template.push('<select ng-change="change('+index+')" class="ad-select" ng-model="'+item+'" ng-options="'+item+'.name for '+item+' in '+item+'s"></select>\n');
					});
					template.push('</div>');
					return template.join("");
				},
				link:function($scope,$element,$attrs){
					var bindModel = $attrs.bindModel;
					var ngModels = bindModel.split(',');
					var $parent = $scope.$parent;
					var defItem = {code:"",name:"请选择",$$index:-1};
					angular.forEach(ngModels,function(item){
						$scope[item+"s"] = [defItem];
						$scope[item] = defItem;
					});
					
					$scope.refreshADInfo = function(code,index){
						if(index<ngModels.length){
							$http.get("http://"+getADHost()+"/ad/"+code+"/childs").then(function(resp){
								var items = resp.data;
								angular.forEach(items,function(item){
									item.$$index = index;
								});
								items.unshift(defItem);
								$scope[ngModels[index]+"s"] = items;
								$scope[ngModels[index]] = defItem;
								for(var i = index+1;i<ngModels.length;i++){
									$scope[ngModels[i]+"s"] = [defItem];
									$scope[ngModels[i]] = defItem;
								}
							});
						}
					};
					$scope.loadADInfo = function(parent,index){
						if(isLocal() && index<3){
							var childs;
							var items = [defItem];
							if(index===0){
								childs = window.adData;
							}else{
								childs = parent.childs;
							}
							angular.forEach(childs,function(item){
								items.push({id:item.i,name:unescape(item.n.replace(/\\u/g, "%u")),childs:item.c,$$index:index});
							});
							$scope[ngModels[index]+"s"] = items;
							$scope[ngModels[index]] = defItem;
							for(var i = index+1;i<ngModels.length;i++){
								$scope[ngModels[i]+"s"] = [defItem];
								$scope[ngModels[i]] = defItem;
							}
						}else{
							$scope.refreshADInfo(parent.id,index);
						}
						
					};
					$scope.loadADInfo({id:0},0);
					$scope.change = function(index){
						var ngModelName = ngModels[index];
						var item = $scope[ngModelName];
						$parent[ngModelName] = item;
					};
					
					var watchHandler = function(newValue){
						$timeout(function(){
							if(newValue){
								var index = newValue.$$index;
								var items = $scope[ngModels[index] + "s"];
								for(var i = 0;i<items.length;i++){
									var item = items[i];
									if((newValue.id && item.id === newValue.id) || item.name === newValue.name){
										$scope[ngModels[index]] = item;
										$scope.loadADInfo(item,index+1);
										break;
									}
								}
							}
		    	 		},0);
					};
					
					for(var i = 0;i<ngModels.length;i++){
						$parent.$watch(ngModels[i],watchHandler);
					}
				}
			};
		}]);
})(window, window.angular);
