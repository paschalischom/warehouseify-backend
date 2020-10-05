package com.uoi.spmsearch.service;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class CallbackFuture extends CompletableFuture<Response> implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {
        super.completeExceptionally(e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        super.complete(response);
    }
}
