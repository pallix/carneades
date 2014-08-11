# Copyright (c) 2014 Fraunhofer Gesellschaft
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

define [
  'angular',
  'angular-translate',
  './argumentsControllers',
  '../../../common/resources/statements',
  '../../../common/resources/theory',
  '../../../common/directives/evaluation-indicator/evaluation-indicator'
  '../../../common/services/projectInfo'
], (angular) ->
  angular.module('arguments.states', [
    'arguments.controllers',
    'resources.statements',
    'resources.theories',
    'directives.evaluationIndicator',
    'services.projectInfo'
  ])

  .config ($stateProvider) ->
    states = [
      name: "home.projects.project.arguments"
      abstract: true
      url: "/:db/arguments"
      resolve:
        project: (ProjectLoader, $stateParams) ->
          return new ProjectLoader $stateParams
    ,
      name: "home.projects.project.arguments.new"
      label: "Argument"
      url: "/new"
      data:
        commands: ['home.projects.project.map','home.projects.project.outline']
      views:
        "content@":
          templateUrl: "projects/project/arguments/argument/edit.jade"
          controller: 'ArgumentNewCtrl'
          resolve:
            theory: 'Theory'
            projectInfo: 'projectInfo'
            statements: (MultiStatementLoader) ->
              return new MultiStatementLoader()
    ]

    angular.forEach states, (state) ->
      $stateProvider.state state
      undefined

    undefined
