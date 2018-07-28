angular.module('myApp.password', [ 'ngRoute' ])

	.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/password', {
			templateUrl : 'login/password.html',
			controller : 'PasswordCtrl'
		});
	} ])

	.controller('PasswordCtrl', [ '$scope', '$rootScope', '$http', '$location', 'UserDataProvider',
		function($scope, $rootScope, $http, $location, authService) {
			$scope.info = false;
			$rootScope.selectedTab = $location.path() || '/';

			$scope.change = function() {
				$http({
					url : 'auth/change-password',
					method : 'POST',
					data : $scope.changePassword,
					headers : authService.createAuthorizationTokenHeader()
				})
					.success(function(res) {
						$scope.changePassword = null;
						$scope.info = true;
					});
			};
		} ]);