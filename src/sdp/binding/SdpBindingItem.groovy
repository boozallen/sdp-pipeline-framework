/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.binding

/*
    A base class for objects that will be stored in the 
    script binding.  Extending this base class will protect
    these objects from being overridden during initialization,
    by library developers, or by pipeline templates.
*/
interface SdpBindingItem extends Serializable{
    // gets called during SDP initialization
    abstract void throwPreLockException()

    //gets called after SDP initialization
    abstract void throwPostLockException()
}