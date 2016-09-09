(function(window,angular){
	'use strict';
	window.adhost = "127.0.0.1:80";
	angular.module('demo',['cjoop.ad'])
	.controller('DemoCtrl',['$scope',function($scope){
		$scope.a = {name:'陕西省',$$index:0};
		$scope.b = {name:'延安市',$$index:1};//name回显
		$scope.c = {id:'610625',$$index:2};//id回显

		$scope.clean = function(){
			$scope.a.name = '';
		};
	}]);
})(window,angular);
