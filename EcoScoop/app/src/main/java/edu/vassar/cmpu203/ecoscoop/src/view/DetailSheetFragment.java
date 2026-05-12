package edu.vassar.cmpu203.ecoscoop.src.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import edu.vassar.cmpu203.ecoscoop.R;

/**
 * Generic bottom sheet for a single weather metric.
 * Shows a large value, three summary chips, and an optional scrollable data table.
 * All content is passed via {@link #newInstance}.
 */
public class DetailSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_EMOJI       = "emoji";
    private static final String ARG_TITLE       = "title";
    private static final String ARG_BIG_VALUE   = "bigValue";
    private static final String ARG_CHIP_LABELS = "chipLabels";
    private static final String ARG_CHIP_VALUES = "chipValues";
    private static final String ARG_ROW_LABELS  = "rowLabels";
    private static final String ARG_ROW_VALUES  = "rowValues";
    private static final String ARG_TABLE_NOTE  = "tableNote";

    public static DetailSheetFragment newInstance(
            String emoji, String title, String bigValue,
            String[] chipLabels, String[] chipValues,
            String[] rowLabels, String[] rowValues,
            String tableNote) {
        Bundle args = new Bundle();
        args.putString(ARG_EMOJI, emoji);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BIG_VALUE, bigValue);
        args.putStringArray(ARG_CHIP_LABELS, chipLabels);
        args.putStringArray(ARG_CHIP_VALUES, chipValues);
        args.putStringArray(ARG_ROW_LABELS, rowLabels);
        args.putStringArray(ARG_ROW_VALUES, rowValues);
        args.putString(ARG_TABLE_NOTE, tableNote);
        DetailSheetFragment f = new DetailSheetFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = requireArguments();

        ((TextView) view.findViewById(R.id.sheetEmoji)).setText(args.getString(ARG_EMOJI));
        ((TextView) view.findViewById(R.id.sheetTitle)).setText(args.getString(ARG_TITLE));
        ((TextView) view.findViewById(R.id.sheetBigValue)).setText(args.getString(ARG_BIG_VALUE));

        String[] chipLabels = args.getStringArray(ARG_CHIP_LABELS);
        String[] chipValues = args.getStringArray(ARG_CHIP_VALUES);
        int[] valueIds = {R.id.chipValue0, R.id.chipValue1, R.id.chipValue2};
        int[] labelIds = {R.id.chipLabel0, R.id.chipLabel1, R.id.chipLabel2};
        for (int i = 0; i < 3 && chipLabels != null && i < chipLabels.length; i++) {
            ((TextView) view.findViewById(valueIds[i])).setText(chipValues[i]);
            ((TextView) view.findViewById(labelIds[i])).setText(chipLabels[i]);
        }

        String[] rowLabels = args.getStringArray(ARG_ROW_LABELS);
        String[] rowValues = args.getStringArray(ARG_ROW_VALUES);

        if (rowLabels != null && rowLabels.length > 0) {
            TextView noteView = view.findViewById(R.id.tableNote);
            noteView.setText(args.getString(ARG_TABLE_NOTE));
            noteView.setVisibility(View.VISIBLE);
            view.findViewById(R.id.tableDivider).setVisibility(View.VISIBLE);

            LinearLayout container = view.findViewById(R.id.tableContainer);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (int i = 0; i < rowLabels.length; i++) {
                View row = inflater.inflate(R.layout.item_sheet_row, container, false);
                ((TextView) row.findViewById(R.id.rowLabel)).setText(rowLabels[i]);
                ((TextView) row.findViewById(R.id.rowValue)).setText(rowValues[i]);
                if (i % 2 == 0) row.setBackgroundColor(0x08000000);
                container.addView(row);
            }
        }
    }
}
