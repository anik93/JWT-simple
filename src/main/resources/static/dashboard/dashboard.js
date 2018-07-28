'use strict';

angular.module('myApp.dashboard', [ 'ngRoute' ])
	.config([ '$routeProvider', function($routeProvider) {
		$routeProvider.when('/', {
			templateUrl : 'dashboard/dashboard.html',
			controller : DashboardCtrl,
			resolve : DashboardCtrl.resolve
		});
	} ]);

function DashboardCtrl($scope, $rootScope, $http, isAuthenticated, authService) {
	$rootScope.authenticated = isAuthenticated;

	$scope.serverResponse = '';
	$scope.alertStatus = '';

	var setResponse = function(res, success) {
		$rootScope.authenticated = isAuthenticated;
		if (success) {
			$scope.alertStatus = 'alert-success';
		} else {
			$scope.alertStatus = 'alert-danger';
		}
		$scope.serverResponse = res;
		$scope.serverResponse.data = JSON.stringify(res.data, null, 2);
	}

	if ($rootScope.authenticated) {
		authService.getUser()
			.then(function(response) {
				$scope.user = response.data;
			});
	}

	$scope.getUserInfo = function() {
		authService.getUser()
			.then(function(response) {
				setResponse(response, true);
			})
			.catch(function(response) {
				setResponse(response, false);
			});
	}

	$scope.getAllUserInfo = function() {
		$http({
			headers : authService.createAuthorizationTokenHeader(),
			method : 'GET',
			url : 'api/user/all'
		})
			.then(function(res) {
				setResponse(res, true);
			})
			.catch(function(response) {
				setResponse(response, false);
			});
	}
}
DashboardCtrl.resolve = {
	isAuthenticated : function($q, $http, UserDataProvider) {
		var deferred = $q.defer();
		var oldToken = UserDataProvider.getJwtToken();
		if (oldToken !== null) {
			$http({
				headers : UserDataProvider.createAuthorizationTokenHeader(),
				method : 'POST',
				url : 'auth/refresh'
			})
				.success(function(res) {
					UserDataProvider.setJwtToken(res.access_token);
					deferred.resolve(res.access_token !== null);
				})
				.error(function(err) {
					UserDataProvider.removeJwtToken();
					deferred.resolve(false);
				});
		} else {
			deferred.resolve(false);
		}
		return deferred.promise;
	}
};

DashboardCtrl.$inject = [ '$scope', '$rootScope', '$http', 'isAuthenticated', 'UserDataProvider' ];