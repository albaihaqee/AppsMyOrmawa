package com.inovarka.myormawa.adapters;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inovarka.myormawa.R;
import com.inovarka.myormawa.models.FormField;

import java.util.ArrayList;
import java.util.List;

public class DynamicFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FormField> fields;
    private final OnFileClickListener fileClickListener;

    public interface OnFileClickListener {
        void onFileClick(FormField field);
    }

    // Constructor lama (tetap disediakan biar tidak error)
    public DynamicFormAdapter(List<FormField> fields) {
        this.fields = fields != null ? fields : new ArrayList<>();
        this.fileClickListener = null;
    }

    // Constructor baru yang mendukung upload file
    public DynamicFormAdapter(List<FormField> fields, OnFileClickListener listener) {
        this.fields = fields != null ? fields : new ArrayList<>();
        this.fileClickListener = listener;
    }

    // View types
    private static final int TYPE_TEXT = 1;
    private static final int TYPE_NUMBER = 2;
    private static final int TYPE_TEXTAREA = 3;
    private static final int TYPE_RADIO = 4;
    private static final int TYPE_SELECT = 5;
    private static final int TYPE_FILE = 6;

    @Override
    public int getItemViewType(int position) {
        FormField field = fields.get(position);
        switch (field.getTipe()) {
            case FormField.TYPE_NUMBER: return TYPE_NUMBER;
            case FormField.TYPE_TEXTAREA: return TYPE_TEXTAREA;
            case FormField.TYPE_RADIO: return TYPE_RADIO;
            case FormField.TYPE_SELECT: return TYPE_SELECT;
            case FormField.TYPE_FILE: return TYPE_FILE;
            default: return TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_TEXT:
            case TYPE_NUMBER:
                return new TextFieldViewHolder(
                        inflater.inflate(R.layout.item_form_text_field, parent, false)
                );
            case TYPE_TEXTAREA:
                return new TextAreaViewHolder(
                        inflater.inflate(R.layout.item_form_textarea, parent, false)
                );
            case TYPE_RADIO:
                return new RadioViewHolder(
                        inflater.inflate(R.layout.item_form_radio, parent, false)
                );
            case TYPE_SELECT:
                return new SelectViewHolder(
                        inflater.inflate(R.layout.item_form_select, parent, false)
                );
            case TYPE_FILE:
                return new FileViewHolder(
                        inflater.inflate(R.layout.item_form_file, parent, false)
                );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {
        FormField field = fields.get(position);

        if (holder instanceof TextFieldViewHolder)
            ((TextFieldViewHolder) holder).bind(field);

        else if (holder instanceof TextAreaViewHolder)
            ((TextAreaViewHolder) holder).bind(field);

        else if (holder instanceof RadioViewHolder)
            ((RadioViewHolder) holder).bind(field);

        else if (holder instanceof SelectViewHolder)
            ((SelectViewHolder) holder).bind(field);

        else if (holder instanceof FileViewHolder)
            ((FileViewHolder) holder).bind(field, fileClickListener);
    }

    @Override
    public int getItemCount() {
        return fields.size();
    }

    public List<FormField> getFields() {
        return fields;
    }

    // ------------------ ViewHolders ---------------------

    static class TextFieldViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        EditText inputField;

        public TextFieldViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.txt_field_label);
            inputField = itemView.findViewById(R.id.et_field_input);
        }

        public void bind(FormField field) {
            labelText.setText(field.getLabel());

            if (field.getTipe().equals(FormField.TYPE_NUMBER))
                inputField.setInputType(InputType.TYPE_CLASS_NUMBER);

            inputField.setText(field.getValue() == null ? "" : field.getValue());

            inputField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    field.setValue(s.toString());
                }
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    static class TextAreaViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        EditText inputField;

        public TextAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.txt_field_label);
            inputField = itemView.findViewById(R.id.et_field_textarea);
        }

        public void bind(FormField field) {
            labelText.setText(field.getLabel());

            inputField.setText(field.getValue() == null ? "" : field.getValue());

            inputField.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    field.setValue(s.toString());
                }
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    static class RadioViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        RadioGroup radioGroup;

        public RadioViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.txt_field_label);
            radioGroup = itemView.findViewById(R.id.rg_options);
        }

        public void bind(FormField field) {
            labelText.setText(field.getLabel());

            radioGroup.removeAllViews();

            if (field.getOptions() != null) {
                for (String opt : field.getOptions()) {
                    RadioButton rb = new RadioButton(itemView.getContext());
                    rb.setText(opt);

                    if (opt.equals(field.getValue()))
                        rb.setChecked(true);

                    radioGroup.addView(rb);
                }
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton rb = group.findViewById(checkedId);
                if (rb != null) field.setValue(rb.getText().toString());
            });
        }
    }

    static class SelectViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        Spinner spinner;

        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.txt_field_label);
            spinner = itemView.findViewById(R.id.sp_options);
        }

        public void bind(FormField field) {
            labelText.setText(field.getLabel());

            List<String> ops = new ArrayList<>();
            ops.add("-- Pilih --");
            ops.addAll(field.getOptions());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_spinner_item,
                    ops
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(adapter);

            if (field.getValue() != null) {
                int pos = ops.indexOf(field.getValue());
                if (pos >= 0) spinner.setSelection(pos);
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (pos == 0) field.setValue(null);
                    else field.setValue(ops.get(pos));
                }
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView labelText, fileNameText;
        View btnChooseFile;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.txt_field_label);
            fileNameText = itemView.findViewById(R.id.txt_file_name);
            btnChooseFile = itemView.findViewById(R.id.btn_choose_file);
        }

        public void bind(FormField field, OnFileClickListener listener) {
            labelText.setText(field.getLabel());

            if (field.getValue() != null && !field.getValue().isEmpty()) {
                fileNameText.setText(field.getValue());
                fileNameText.setVisibility(View.VISIBLE);
            } else {
                fileNameText.setVisibility(View.GONE);
            }

            btnChooseFile.setOnClickListener(v -> {
                if (listener != null) listener.onFileClick(field);
            });
        }
    }
}
