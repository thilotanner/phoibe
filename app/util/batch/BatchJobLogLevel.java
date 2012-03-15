////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright (c) Astina AG. All Rights Reserved.
//
// This software is the confidential and proprietary information of Astina AG
// ("Confidential Information"). You shall not disclose such Confidential Information and shall use
// it only in accordance with the terms of the license agreement you entered into with Astina.
//
// ASTINA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ASTINA SHALL NOT BE LIABLE FOR ANY DAMAGES
// SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
// DERIVATIVES.
////////////////////////////////////////////////////////////////////////////////////////////////////
package util.batch;

/**
 * @author $Author: ttanner $
 * @version $Revision: 1853 $, $Date: 2011-02-15 16:02:41 +0100 (Di, 15 Feb 2011) $
 */
public enum BatchJobLogLevel
{
    TRACE("TRACE"),

    DEBUG("DEBUG"),

    INFO("INFO "),

    WARN("WARN "),

    ERROR("ERROR");

    private String levelString;

    BatchJobLogLevel(String levelString)
    {
        this.levelString = levelString;
    }

    public String getLevelString()
    {
        return levelString;
    }
}
