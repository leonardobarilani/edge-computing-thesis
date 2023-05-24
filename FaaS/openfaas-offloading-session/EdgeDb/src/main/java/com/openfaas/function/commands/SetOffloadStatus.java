package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

@RequiresQueryAnnotation.RequiresQuery(query = "status")
public class SetOffloadStatus implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String offloading = req.getQuery().get("status");

        if (!offloading.equals("accept") && !offloading.equals("reject")) {
            String message = "Malformed request: <" + offloading + "> is not a valid offloading status (valid offloading statuses: accept/reject)";

            Logger.log(message);
            res.setBody(message);
            res.setStatusCode(400);
        } else {
            String message = "Offloading status from <" + ConfigurationDAO.getOffloading() + "> to <" + offloading + ">";
            if (offloading.equals("accept"))
                ConfigurationDAO.acceptOffloading();
            else
                ConfigurationDAO.rejectOffloading();

            Logger.log(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
    }
}
