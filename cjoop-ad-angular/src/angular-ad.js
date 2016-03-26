/**
 * @license cjoop-ad v0.0.1
 * (c) 2010-2015 cjoop, Inc. http://www.cjoop.com
 * License: MIT
 */
(function(window, angular) {
	'use strict';
	angular.module('cjoop.ad',[])
		
		.run(function($templateCache){
			$templateCache.put('cjoop/ad-template.html',
			"test ok\n"
			);
		})
		.directive('ad',function($http){
			return {
				scope:{},
				templateUrl:'cjoop/ad-template.html',
				link:function($scope,element,$attrs){
					console.log($http,$scope,element,$attrs);
				}
			};
		});
})(window, window.angular);
