/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package sdp.extensions

import java.lang.annotation.Retention
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
    Will get triggered after every pipeline step 
    TODO: 
        maybe enhance to also after every Stage? 
*/
@Retention(RUNTIME)
public @interface AfterStep{}