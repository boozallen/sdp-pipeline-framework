/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

import sdp.binding.*

import org.jenkinsci.plugins.workflow.libs.LibraryRecord
import org.jenkinsci.plugins.workflow.libs.LibrariesAction
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.cps.CpsThread
import com.cloudbees.groovy.cps.NonCPS
import groovy.io.FileType

void call(script){
  pipeline_config().libraries.each{ lib_name, lib_config ->
    branch = lib_config.branch
    config = lib_config.subMap(lib_config.keySet() - "branch")

    if( library_defined_by_jenkins(lib_name) ){
      library "${lib_name}@${branch ?: "master"}"
      get_external_library_steps(lib_name).each{ step ->
        def step_impl = getProperty(step)
        step_impl.metaClass."config" = config
        def step_wrapper = new StepWrapper(script, step_impl, step, lib_name)
        script.getBinding().setVariable(step, step_wrapper)
      }
    }else if( library_defined_by_sdp(lib_name) ){
      println "Loading Libary ${lib_name} From SDP Monorepo"
      if (branch) println """
        Warning! SDP libraries can't specify different branches to load.
        Ignoring branch configuration [${branch}] for library [${lib_name}]
      """
      get_sdp_library_steps(lib_name).each{ step, impl ->
        def transformed_impl = sdp_evaluate("${impl}; return this", script.getBinding())
        transformed_impl.metaClass.config = config
        def step_wrapper = new StepWrapper(script, transformed_impl, step, lib_name)
        script.getBinding().setVariable(step, step_wrapper)
      }
    }else{
      error "Library ${lib_name} not defined in Jenkins or SDP monorepo"
    }
  }
}

Boolean library_defined_by_jenkins(String lib_name){
  return (lib_name in GlobalLibraries.get().getLibraries()*.name)
}

@NonCPS
Boolean library_defined_by_sdp(String lib_name){
  build_root = CpsThread.current().getExecution().getOwner().getExecutable().getRootDir()
  def file = new File("${build_root}/libs/solutions_delivery_platform/resources/sdp/libraries/${lib_name}")
  return file.isDirectory()
}

def get_external_library_steps(String name){
  LibrariesAction action = $build().getAction(LibrariesAction.class);
  return action.getLibraries().find{ it.name.equals(name) }.variables
}

@NonCPS
def get_sdp_library_steps(name){
  build_root = CpsThread.current().getExecution().getOwner().getExecutable().getRootDir()
  def steps = [:]
  new File("${build_root}/libs/solutions_delivery_platform/resources/sdp/libraries/${name}").traverse(type: FileType.FILES){ file ->
    if (file.path.endsWith(".groovy"))
      steps[file.name.take(file.name.lastIndexOf('.'))] = file.text
  }
  return steps
}
