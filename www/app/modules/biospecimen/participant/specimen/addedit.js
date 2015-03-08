
angular.module('os.biospecimen.specimen.addedit', [])
  .controller('AddEditSpecimenCtrl', function($scope, $state, cpr, visit, specimen, PvManager) {
    function loadPvs() {

      $scope.loadSpecimenTypes = function(specimenClass, notclear) {
        $scope.specimenTypes = PvManager.getPvsByParent('specimen-class', specimenClass);
        if (!notclear) {
          $scope.currSpecimen.type = '';
        }
      };

      $scope.collectionStatuses = PvManager.getPvs('specimen-status');
      $scope.specimenClasses = PvManager.getPvs('specimen-class');
      if (!!specimen.specimenClass) {
        $scope.loadSpecimenTypes(specimen.specimenClass, true);
      }
      $scope.anatomicSites = PvManager.getPvs('anatomic-site');
      $scope.lateralities = PvManager.getPvs('laterality');
      $scope.pathologyStatuses = PvManager.getPvs('pathology-status');
    };

    function init() {
      loadPvs();

      var currSpecimen = $scope.currSpecimen = angular.copy(specimen);
      currSpecimen.visitId = visit.id;

      if (currSpecimen.status != 'Collected') {
        currSpecimen.status = 'Collected';
        currSpecimen.availableQty = currSpecimen.initialQty;
      }

      if (!currSpecimen.storageLocation) {
        currSpecimen.storageLocation = {};
      }
    }

    $scope.saveSpecimen = function() {
      $scope.currSpecimen.$saveOrUpdate().then(
        function(result) {
          angular.extend($scope.specimen, result);
          var params = {specimenId: result.id, srId: result.reqId};
          $state.go('specimen-detail.overview', params);
        }
      );
    }

    init();
  });