# Copyright (c) 2014 Fraunhofer Gesellschaft
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
define [
  'angular',
  'angular-translate'
], (angular) ->

  return angular.module('legalprofile.controllers', [
    'pascalprecht.translate'
  ])

  .controller 'LegalprofileViewCtrl', ($scope, $translate, $state, $stateParams, theory, legalprofile, legalprofileInfo, scroll, project) ->
        
    _title = legalprofile.metadata.title + ' ' + ($translate.instant 'projects.legalprofile')
    
    _edit = ->
      url = 'home.projects.project.legalprofiles.legalprofile.edit'
      $state.go url, $stateParams, reload: true

    _delete = ->
      legalprofile.pid = $stateParams.pid      
      legalprofile.lpid = $stateParams.lpid      
      legalprofile.$delete({}, () ->
        url = 'home.projects.project.legalprofiles'
        $state.go url, $stateParams, reload: true)
      
    $scope.title = _title
    $scope.section = theory
    $scope.isSchemeOut = legalprofileInfo.isSchemeOut
    $scope.isSchemeIn = legalprofileInfo.isSchemeIn
    $scope.isSchemeUndecided = legalprofileInfo.isSchemeUndecided
    $scope.project = project
    $scope.edit = _edit
    $scope.legalprofile = legalprofile
    $scope.delete = _delete

    if $stateParams.scrollTo?
      scroll.scrollTo $stateParams.scrollTo
      console.log 'lp', legalprofile
      
    return undefined

  .controller 'LegalprofileEditCtrl', ($scope, $translate, $state, $stateParams, theory, legalprofile, legalprofileInfo, Legalprofile, scroll, project) ->
    _title = legalprofile.metadata.title + ' ' + ($translate.instant 'projects.legalprofile')
    
    _save = ->
      Legalprofile.update($stateParams, legalprofile).$promise.then((data) ->
        url = 'home.projects.project.legalprofiles.legalprofile'
        $state.go url, $stateParams, reload: true)

    _cancel = ->
      url = 'home.projects.project.legalprofiles.legalprofile'
      $state.go url, $stateParams

    _getRuleIndex = (scheme) ->
      for rule, idx in legalprofile.rules
        if rule.ruleid == scheme.id
          return idx
          
      return undefined
      
    $scope.title = _title
    $scope.section = theory
    $scope.isSchemeOut = legalprofileInfo.isSchemeOut
    $scope.isSchemeIn = legalprofileInfo.isSchemeIn
    $scope.isSchemeUndecided = legalprofileInfo.isSchemeUndecided
    $scope.project = project
    $scope.legalprofile = legalprofile
    $scope.getRuleIndex = _getRuleIndex
    $scope.save = _save
    $scope.cancel = _cancel

    $scope.valueOptions = [
      {value: 1.0, name: "in"},
      {value: 0.0, name: "out"},
      {value: 0.5, name: "undecided"},
    ]

    if $stateParams.scrollTo?
      scroll.scrollTo $stateParams.scrollTo
      
    return undefined

