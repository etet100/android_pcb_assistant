package bts.pcbassistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import bts.pcbassistant.search.SearchActivity_;
import bts.pcbassistant.selectFile.SelectFileActivity_;

public class IntentFactory {

    public static Intent createOpenFileIntent(Context ctx, String path, String source) {
        Intent intent = new Intent(ctx, SelectFileActivity_.class);
        intent.setData(Uri.parse("test.a"));
        intent.putExtra("PATH", path);
        intent.putExtra("SOURCE", source);
        return intent;
    }

    public static Intent createExternalOpenFileIntent(Context ctx) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("file/*");
        return intent;
    }

    public static Intent searchIntent(Context ctx) {
        Intent intent = new Intent(ctx, SearchActivity_.class);
        return intent;
    }

    public static Intent openWorkspaceWithFileIntent(Context ctx, String name, boolean board, boolean schematic) {
        Intent intent = new Intent(ctx, WorkspaceActivity_.class);
        intent.putExtra("action", "openFile");
        intent.putExtra("fileName", name);
        intent.putExtra("openBoard", board);
        intent.putExtra("openSchematic", schematic);
        return intent;
    }

}
