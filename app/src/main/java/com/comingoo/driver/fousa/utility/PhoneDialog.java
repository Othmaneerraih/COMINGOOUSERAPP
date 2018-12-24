package com.comingoo.driver.fousa.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.comingoo.driver.fousa.R;

public class PhoneDialog  extends AppCompatDialogFragment {

    private EditText confirmCode;
    private PhoneDialog.PhoneDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceSatte) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_subscribe_phone, null);

        confirmCode = (EditText) view.findViewById(R.id.confirmCode);

        view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.applyTextCode(confirmCode.getText().toString());
                dismiss();
            }
        });



        builder.setView(view);
        return builder.create();
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PhoneDialog.PhoneDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }

    }

    public interface PhoneDialogListener{
        void applyTextCode(String code);
    }


}
