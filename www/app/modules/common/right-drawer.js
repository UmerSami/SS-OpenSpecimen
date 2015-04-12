angular.module('openspecimen')
  .directive('osRightDrawer', function(osRightDrawerSvc) {
    return {
      restrict: 'A',

      link: function(scope, element, attrs) {
        element.addClass('os-right-drawer');
        element.removeAttr('os-right-drawer');

        var header = element.find('div.os-head');
        if (header) {
          var divider = angular.element('<div/>')
            .addClass('os-divider');
          header.after(divider);
        }

        osRightDrawerSvc.setDrawer(element);
      }
    };
  })
  .directive('osRightDrawerToggle', function(osRightDrawerSvc) {
    return {
      restrict: 'A',
      link: function(scope, element, attrs) {
        element.on('click', function() {
          osRightDrawerSvc.toggle();
        });
      }
    };
  })
  .factory('osRightDrawerSvc', function() {
    var drawerEl = undefined;
    return {
      setDrawer: function(drawer) {
        drawerEl = drawer;
      },

      toggle: function() {
        drawerEl.toggleClass('active');

        var cardsDiv = drawerEl.parent().find("div.os-cards, div.os-list-container");
        if (drawerEl.hasClass('active')) {
          cardsDiv.css("width", "75%");
          drawerEl.find('input, textArea, select, button').filter(':visible:first').focus();
        } else {
          cardsDiv.css("width", "100%");
        }
      }
    }
  });
