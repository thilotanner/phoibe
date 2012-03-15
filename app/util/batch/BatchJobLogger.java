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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author $Author: ttanner $
 * @version $Revision: 1853 $, $Date: 2011-02-15 16:02:41 +0100 (Di, 15 Feb 2011) $
 */
public class BatchJobLogger
{
    private StringBuilder logMessages;
    private DateFormat dateFormat;

    public BatchJobLogger()
    {
        this.logMessages = new StringBuilder();
        this.dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
    }

    public void trace(String message)
    {
        log(BatchJobLogLevel.TRACE, message);
    }

    public void debug(String message)
    {
        log(BatchJobLogLevel.DEBUG, message);
    }

    public void info(String message)
    {
        log(BatchJobLogLevel.INFO, message);
    }

    public void warn(String message)
    {
        log(BatchJobLogLevel.WARN, message);
    }

    public void error(String message)
    {
        log(BatchJobLogLevel.ERROR, message);
    }

    @Override
    public String toString()
    {
        return logMessages.toString();
    }

    private void log(BatchJobLogLevel level, String message)
    {
        logMessages.append(dateFormat.format(new Date()));
        logMessages.append(" ");
        logMessages.append(level.getLevelString());
        logMessages.append(" ~ ");
        logMessages.append(message);
        logMessages.append("\n");
    }
}
