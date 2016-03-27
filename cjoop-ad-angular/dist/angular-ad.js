/*! cjoop-ad-angular - v0.0.1 - 2016-03-27
* https://github.com/cjjava/cjoop-ad
* Copyright (c) 2016 cjjava <85309651@qq.com>; Licensed MIT */
(function(window, angular) {
	'use strict';
	angular.module('cjoop.ad',[])
		
		.run(['$templateCache',function($templateCache){
			$templateCache.put('cjoop/ad-template.html',
			"test ok\n"
			);
		}])
		.directive('ad',['$http',function($http){
			return {
				scope:{},
				templateUrl:'cjoop/ad-template.html',
				link:function($scope,$element,$attrs){
					console.log($http,$scope,$element,$attrs);
					$element.click(function(){
						
					});
				}
			};
		}]);
})(window, window.angular);
