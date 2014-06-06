# Copyright (c) 2014 Fraunhofer Gesellschaft
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

define [
  'angular',
  '../../../common/resources/metadata',
  '../../../common/resources/references',
  '../../../common/resources/outline',
  '../../../common/resources/issues'
  '../../../common/services/scroll'], (angular) ->

  angular.module('outline.states', [
    'resources.metadata',
    'resources.metadata.references', 'resources.outline',
    'resources.outline.issues', 'services.scroll']
  ).config(($stateProvider, $stateUtilProvider) ->
    emptyReferences = (references) ->
      (v for k,v of references when v? and k != '$promise' and k != '$resolved').length is 0
    helper = $stateUtilProvider.$get()
    states = [
      {
        name: 'home.projects.project.outline'
        label: 'Outline'
        url: '/:db/outline?scrollTo'
        views:
          "nav@":
            template: "<bc-navigation></bc-navigation>"
          "subnav@":
            templateUrl: 'subnav.tpl.html'
            resolve: helper.builder().add('commands', helper.cmdBuilder('home.projects.project.map', 'home.projects.project.theory')).build()
            controller: 'SubnavController'
          "content@":
            templateUrl: 'project/outline/outline-main.tpl.html'
            controller: ($scope, $stateParams, scroll, project, tproject, references) ->
              $scope.project = project
              $scope.project.title = project.title
              $scope.scrollTo = scroll.scrollTo

              getSchemesProject = (project) ->
                schemes = project.schemes
                res = schemes.split '/'
                if res.length == 1 then project.id else res[0]

              getSchemesName = (project) ->
                schemes = project.schemes
                res = schemes.split '/'
                if res.length == 1 then res[0] else res[1]

              $scope.$stateParams.tpid = getSchemesProject(tproject)
              $scope.$stateParams.tid = getSchemesName(tproject)

              $scope.hasReferences = not emptyReferences references
              if $stateParams.scrollTo?
                scroll.scrollTo $stateParams.scrollTo

            resolve:
              tproject: (ProjectLoader, $stateParams) ->
                new ProjectLoader($stateParams)
              project: ($stateParams, MetadataLoader) ->
                $stateParams.mid = 1
                new MetadataLoader($stateParams)
              scroll: 'scroll'
              references: ($stateParams, MultiReferenceLoader) ->
                $stateParams.mid = undefined
                return new MultiReferenceLoader($stateParams)

          "issues@home.projects.project.outline":
            templateUrl: 'project/outline/issues.tpl.html'
            controller: ($scope, issues) ->
              $scope.issues = issues
            resolve:
              issues: ($stateParams, MultiIssueLoader) ->
                return new MultiIssueLoader($stateParams)

          "references@home.projects.project.outline":
            templateUrl: 'project/outline/references.tpl.html'
            controller: ($scope, references) ->
              $scope.references = references
              $scope.hasReferences = not emptyReferences references
            resolve:
              references: ($stateParams, MultiReferenceLoader) ->
                $stateParams.mid = undefined
                return new MultiReferenceLoader($stateParams)

          "outline@home.projects.project.outline":
            templateUrl: 'project/outline/outline.tpl.html'
            controller: ($scope, outline) ->
              $scope.outline = outline
            resolve:
              outline: ($stateParams, MultiOutlineLoader) ->
                return new MultiOutlineLoader($stateParams)
      }
    ]

    angular.forEach states, (state) ->
      $stateProvider.state state
      undefined

    undefined
  )
