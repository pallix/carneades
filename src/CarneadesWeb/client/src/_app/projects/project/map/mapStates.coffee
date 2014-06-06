# Copyright (c) 2014 Fraunhofer Gesellschaft
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

  define ['angular'], (angular) ->
  angular.module('map.states', ["map.controllers"])
  .config(($stateProvider, $stateUtilProvider) ->
    helper = $stateUtilProvider.$get()
    states = [
      name: "home.projects.project.map"
      label: "Map"
      url: "/:db/map"
      views:
        "nav@":
          template: "<bc-navigation></bc-navigation>"
        "subnav@":
          templateUrl: 'subnav.tpl.html'
          resolve: helper.builder().add('commands', helper.cmdBuilder('home.projects.project.outline','home.projects.project.theory')).build()
          controller: 'SubnavController'
        "content@":
          templateUrl: "project/map/map.tpl.html"
          controller: "MapCtrl"
          resolve:
            map: ($http, $stateParams, $location) ->
              p = $location.protocol()
              h = $location.host()
              po = $location.port()
              pid = $stateParams.pid
              db = $stateParams.db
              $http(
                method: 'GET'
                url: "#{p}://#{h}:#{po}/carneades/api/projects/#{pid}/#{db}/map"
              ).then (data) -> data.data
    ]

    angular.forEach states, (state) -> $stateProvider.state state
  )
