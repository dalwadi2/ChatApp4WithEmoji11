package com.creative.chatapp4.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.creative.chatapp4.R;
import com.creative.chatapp4.core.ChatService;
import com.creative.chatapp4.ui.adapters.SearchListViewAdapter;
import com.creative.chatapp4.ui.adapters.UsersAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewDialogActivity extends BaseActivity implements QBEntityCallback<ArrayList<QBUser>> {

    private static final int PAGE_SIZE = 10;

    private int listViewIndex;
    private int listViewTop;
    private int currentPage = 0;
    private List<QBUser> users = new ArrayList<>();

    private PullToRefreshListView usersList;
    private Button createChatButton;
    private ProgressBar progressBar;
    private UsersAdapter usersAdapter;


    ListView list;
    SearchListViewAdapter adapter;
    EditText editsearch;
    ArrayList<QBUser> arraylist = new ArrayList<QBUser>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new);

        usersList = (PullToRefreshListView) findViewById(R.id.usersList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        editsearch = (EditText) findViewById(R.id.code);
// Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                usersAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        createChatButton = (Button) findViewById(R.id.createChatButton);
        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatService.getInstance().addDialogsUsers(usersAdapter.getSelected());

                // Create new group dialog
                //
                QBDialog dialogToCreate = new QBDialog();
                dialogToCreate.setName(usersListToChatName());
                if (usersAdapter.getSelected().size() == 1) {
                    dialogToCreate.setType(QBDialogType.PRIVATE);
                } else {
                    dialogToCreate.setType(QBDialogType.GROUP);
                }
                dialogToCreate.setOccupantsIds(getUserIds(usersAdapter.getSelected()));

                QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallback<QBDialog>() {
                    @Override
                    public void onSuccess(QBDialog dialog, Bundle args) {
                        if (usersAdapter.getSelected().size() == 1) {
                            startSingleChat(dialog);
                        } else {
                            startGroupChat(dialog);
                        }
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(NewDialogActivity.this);
                        dialog.setMessage("Chat room can't be created,please try again.").create().show();
                    }
                });
            }
        });

        usersList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.
                loadNextPage();
                listViewIndex = usersList.getRefreshableView().getFirstVisiblePosition();
                View v = usersList.getRefreshableView().getChildAt(0);
                listViewTop = (v == null) ? 0 : v.getTop();
            }
        });

        if (isSessionActive()) {
            loadNextPage();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(NewDialogActivity.this, DialogsActivity.class);
        startActivity(i);
        finish();
    }


    public static QBPagedRequestBuilder getQBPagedRequestBuilder(int page) {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(page);
        pagedRequestBuilder.setPerPage(PAGE_SIZE);

        return pagedRequestBuilder;
    }


    @Override
    public void onSuccess(ArrayList<QBUser> newUsers, Bundle bundle) {

        // save users
        //
        users.addAll(newUsers);

        // Prepare users list for simple adapter.
        //
        usersAdapter = new UsersAdapter(users, this);
        usersList.setAdapter(usersAdapter);
        usersList.onRefreshComplete();
        usersList.getRefreshableView().setSelectionFromTop(listViewIndex, listViewTop);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(QBResponseException errors) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("get users errors: " + errors).create().show();
    }


    private String usersListToChatName() {
        String chatName = "";
        for (QBUser user : usersAdapter.getSelected()) {
            String prefix = chatName.equals("") ? "" : ", ";
            chatName = chatName + prefix + user.getLogin();
        }
        return chatName;
    }

    public void startSingleChat(QBDialog dialog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

        ChatActivity.start(this, bundle);
    }

    private void startGroupChat(QBDialog dialog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.EXTRA_DIALOG, dialog);

        ChatActivity.start(this, bundle);
    }

    private void loadNextPage() {
        ++currentPage;

        QBUsers.getUsers(getQBPagedRequestBuilder(currentPage), this);
    }

    public static ArrayList<Integer> getUserIds(List<QBUser> users) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (QBUser user : users) {
            ids.add(user.getId());
        }
        return ids;
    }

    //
    // ApplicationSessionStateCallback
    //

    @Override
    public void onStartSessionRecreation() {

    }

    @Override
    public void onFinishSessionRecreation(final boolean success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success) {
                    loadNextPage();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {

          /*  final Dialog dialog = new Dialog(NewDialogActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.search_user);

            list = (ListView) dialog.findViewById(R.id.listView);

           *//* for (int i = 0; i < countryName.length; i++) {
                Coutrycode wp = new Coutrycode(code[i], countryName[i]);
                // Binds all strings into an array
                arraylist.add(wp);
            }*//*

            // Pass results to ListViewAdapter Class
            adapter = new SearchListViewAdapter(NewDialogActivity.this, arraylist);

            // Binds the Adapter to the ListView
            list.setAdapter(adapter);

            // Locate the EditText in listview_main.xml
            editsearch = (EditText) dialog.findViewById(R.id.code);

            // Capture Text in EditText
            editsearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1,
                                              int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                    // TODO Auto-generated method stub
                }
            });

            dialog.show();*/

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
