package comingoo.vone.tahae.comingoodriver;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SubscribeDialog extends AppCompatDialogFragment {

    private EditText nameEdit;
    private EditText passwordEdit;
    private EditText teleEdit;


    private SubscribeDialogListener listener;

    public Dialog onCreateDialog(Bundle savedInstanceSatte){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.subscribe_dialog, null);

        builder.setView(view);


        nameEdit = view.findViewById(R.id.name);
        teleEdit = view.findViewById(R.id.tele);
        passwordEdit = view.findViewById(R.id.password);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String tele = teleEdit.getText().toString();
                listener.applyText(name, password, tele);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SubscribeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }

    }

    public interface SubscribeDialogListener{
        void applyText(String name, String password, String tele);
    }
}
