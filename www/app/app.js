'use strict';

angular.module('openspecimen', [
  'os.common',
  'os.biospecimen',
  'os.administrative',

  'ngMessages',
  'ngSanitize', 
  'ui.router', 
  'ui.bootstrap', 
  'ui.mask', 
  'ui.keypress', 
  'ui.select',
  'mgcrea.ngStrap.popover',
  'angular-loading-bar',
  'pascalprecht.translate'])

  .config(function(
    $stateProvider, $urlRouterProvider, 
    $httpProvider, $translateProvider,
    uiSelectConfig, ApiUrlsProvider) {

    $translateProvider.useStaticFilesLoader({
      prefix: 'modules/i18n/',
      suffix: '.js'
    });

    $translateProvider.preferredLanguage('en_US');

    $stateProvider
      .state('home', {
        abstract: true,
        templateUrl: 'modules/common/home.html'
      })
      .state('login', {
        url: '/',
        templateUrl: 'modules/user/signin.html',
        controller: 'LoginCtrl',
        parent: 'home'
      })
      .state('signed-in', {
        abstract: true,
        templateUrl: 'modules/common/appmenu.html',
        controller: function($scope, Alerts) {
          $scope.alerts = Alerts.messages;
        }
      })
      .state('sign-up', {
        url: '/signup',
        templateUrl: 'modules/user/signup.html',
        resolve: {
          user: function(User) {
            return new User();
          }
        },
        controller: 'UserAddEditCtrl',
        parent: 'home'
      });

    $urlRouterProvider.otherwise('/');

    $httpProvider.interceptors.push('httpRespInterceptor');

    ApiUrlsProvider.hostname = "localhost"; // used for testing purpose
    ApiUrlsProvider.port = 8180;
    ApiUrlsProvider.secure = false;
    ApiUrlsProvider.app = "/openspecimen";
    ApiUrlsProvider.urls = {
      'sessions': '/rest/ng/sessions',
      'collection-protocols': '/rest/ng/collection-protocols',
      'cprs': '/rest/ng/collection-protocol-registrations',
      'participants': '/rest/ng/participants',
      'sites': '/rest/ng/sites',
      'form-files': '/rest/ng/form-files'
    };

    uiSelectConfig.theme = 'bootstrap';
  })
  .factory('httpRespInterceptor', function($q, $injector, Alerts, $window) {
    return {
      request: function(config) {
        return config || $q.when(config);
      },

      requestError: function(rejection) {
        $q.reject(rejection);
      },

      response: function(response) {
        return response || $q.when(response);
      },

      responseError: function(rejection) {
        if (rejection.status == 401) {
          $window.localStorage['osAuthToken'] = '';
          $injector.get('$state').go('login'); // using injector to get rid of circular dependencies
        } else if (rejection.status / 100 == 5) {
          Alerts.error("Internal server error. Please contact system administrator");
        } else if (rejection.status / 100 == 4) {
          Alerts.error("Bad user action: " + rejection.data.message);
        }

        return $q.reject(rejection);
      }
    };
  })
  .factory('ApiUtil', function($window, $http) {
    return {
      processResp: function(result) {
        var response = {};
        if (result.status / 100 == 2) {
          response.status = "ok";
        } else if (result.status / 100 == 4) {
          response.status = "user_error";
        } else if (result.status / 100 == 5) {
          response.status = "server_error";
        }

        response.data = result.data;
        return response;
      },

      initialize: function(token) {
        if (!token) {
          token = $window.localStorage['osAuthToken'];
          if (!token) {
            return;
          }

          //token = JSON.parse(token);
        }

        $http.defaults.headers.common['X-OS-API-TOKEN'] = token;
        $http.defaults.withCredentials = true;
        //$cookieStore.put('JESSIONID', token);
        //$http.defaults.headers.common['Cookie'] = 'JSESSIONID=' + token;
      }
    };
  })
  .provider('ApiUrls', function() {
    var that = this;

    this.hostname = "";
    this.port = "";
    this.secure = false;
    this.app = "";
    this.urls = {};

    this.$get = function() {
      return {
        hostname: that.hostname,
        port    : that.port,
        secure  : that.secure,
        app     : that.app,
        urls    : that.urls,

        getBaseUrl: function() {
          var prefix = '';
          if (this.hostname) {
            var protocol = this.secure ? 'https://' : 'http://';
            prefix = protocol + this.hostname + ':' + this.port;
          }

          return prefix + this.app + '/rest/ng/';
        },

        getUrl  : function(key) {
          var url = '';
          if (key) {
            url = this.urls[key];
          }

          var prefix = "";
          if (this.hostname) {
            var protocol = this.secure ? 'https://' : 'http://';
            prefix = protocol + this.hostname + ":" + this.port;
          }

          return prefix + this.app + url;
        }
      };
    }
  })
  .run(function($rootScope, $window, ApiUtil) {
    if ($window.localStorage['osAuthToken']) {
      $rootScope.loggedIn = true;
    }

    ApiUtil.initialize();

    $rootScope.$on('$stateChangeSuccess', 
      function(event, toState, toParams, fromState, fromParams) { 
        $rootScope.state = toState;
      });

    $rootScope.back = function() {
      $window.history.back();
    }
  });
