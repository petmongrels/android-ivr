package com.thoughtworks.androidivr.audiostream.detection;

import java.util.ArrayList;
import java.util.List;

/**
 * Original author:       Plyashkevich Viatcheslav <plyashkevich@yandex.ru>
 * http://sourceforge.net/projects/java-dtmf
 */
public class DTMFDetector implements Runnable {
    private List<Character> dialledButtons = new ArrayList<Character>();
    private short indexForDialButtons = 0;

    private static final int COEFF_NUMBER = 18;
    private static final short FREQUENCY_VALUES[] = {27860, 26745, 25529, 24216, 19747, 16384, 12773, 8967, 21319, 29769, 32706, 32210, 31778, 31226, -1009, -12772, -22811, -30555};
    private static final int powerThreshold = 328;
    private static final int dialTonesToOthersTones = 16;
    private static final int dialTonesToOthersDialTones = 6;

    private short pArraySamples[];
    private int T[] = new int[COEFF_NUMBER];
    private short internalArray[];
    private final int SAMPLES = 102;
    private int frame_count;
    private char prevDialButton;
    private boolean permissionFlag;

    private DetectionSpec detectionSpec;
    private SampleProvider sampleProvider;

    public DTMFDetector(DetectionSpec detectionSpec, SampleProvider sampleProvider) {
        this.detectionSpec = detectionSpec;
        this.sampleProvider = sampleProvider;
        dialledButtons.add((char) 0);
        pArraySamples = new short[detectionSpec.frameSize() + SAMPLES];
        internalArray = new short[SAMPLES];
        prevDialButton = ' ';
    }

    private short norm_l(int L_var1) {
        short var_out;

        if (L_var1 == 0) {
            var_out = 0;
        } else {
            if (L_var1 == 0xffffffff) {
                var_out = 31;
            } else {
                if (L_var1 < 0) {
                    L_var1 = ~L_var1;
                }

                for (var_out = 0; L_var1 < 0x40000000; var_out++) {
                    L_var1 <<= 1;
                }
            }
        }
        return var_out;
    }

    private char dtmfDetection(final short short_array_samples[]) {
        int Dial = 32, Sum;
        char return_value = ' ';
        int ii;
        Sum = 0;

        for (ii = 0; ii < SAMPLES; ii++) {
            if (short_array_samples[ii] >= 0)
                Sum += short_array_samples[ii];
            else
                Sum -= short_array_samples[ii];
        }
        Sum /= SAMPLES;
        if (Sum < powerThreshold)
            return ' ';

        for (ii = 0; ii < SAMPLES; ii++) {
            T[0] = (int) (short_array_samples[ii]);
            if (T[0] != 0) {
                if (Dial > norm_l(T[0])) {
                    Dial = norm_l(T[0]);
                }
            }
        }

        Dial -= 16;

        for (ii = 0; ii < SAMPLES; ii++) {
            T[0] = short_array_samples[ii];
            internalArray[ii] = (short) (T[0] << Dial);
        }

        goertzelFilter(FREQUENCY_VALUES[0], FREQUENCY_VALUES[1], internalArray, T, SAMPLES, 0);
        goertzelFilter(FREQUENCY_VALUES[2], FREQUENCY_VALUES[3], internalArray, T, SAMPLES, 2);
        goertzelFilter(FREQUENCY_VALUES[4], FREQUENCY_VALUES[5], internalArray, T, SAMPLES, 4);
        goertzelFilter(FREQUENCY_VALUES[6], FREQUENCY_VALUES[7], internalArray, T, SAMPLES, 6);
        goertzelFilter(FREQUENCY_VALUES[8], FREQUENCY_VALUES[9], internalArray, T, SAMPLES, 8);
        goertzelFilter(FREQUENCY_VALUES[10], FREQUENCY_VALUES[11], internalArray, T, SAMPLES, 10);
        goertzelFilter(FREQUENCY_VALUES[12], FREQUENCY_VALUES[13], internalArray, T, SAMPLES, 12);
        goertzelFilter(FREQUENCY_VALUES[14], FREQUENCY_VALUES[15], internalArray, T, SAMPLES, 14);
        goertzelFilter(FREQUENCY_VALUES[16], FREQUENCY_VALUES[17], internalArray, T, SAMPLES, 16);

        int Row = 0;
        int Temp = 0;

        for (ii = 0; ii < 4; ii++) {
            if (Temp < T[ii]) {
                Row = ii;
                Temp = T[ii];
            }
        }

        int Column = 4;
        Temp = 0;

        for (ii = 4; ii < 8; ii++) {
            if (Temp < T[ii]) {
                Column = ii;
                Temp = T[ii];
            }
        }

        Sum = 0;

        for (ii = 0; ii < 10; ii++) {
            Sum += T[ii];
        }
        Sum -= T[Row];
        Sum -= T[Column];
        Sum >>= 3;

        if (Sum == 0) {
            Sum = 1;
        }

        if (T[Row] / Sum < dialTonesToOthersDialTones)
            return ' ';
        if (T[Column] / Sum < dialTonesToOthersDialTones)
            return ' ';


        if (T[Row] < (T[Column] >> 2)) return ' ';

        if (T[Column] < ((T[Row] >> 1) - (T[Row] >> 3))) return ' ';

        for (ii = 0; ii < COEFF_NUMBER; ii++)
            if (T[ii] == 0)
                T[ii] = 1;

        for (ii = 10; ii < COEFF_NUMBER; ii++) {
            if (T[Row] / T[ii] < dialTonesToOthersTones)
                return ' ';
            if (T[Column] / T[ii] < dialTonesToOthersTones)
                return ' ';
        }

        for (ii = 0; ii < 10; ii++) {
            if (T[ii] != T[Column]) {
                if (T[ii] != T[Row]) {
                    if (T[Row] / T[ii] < dialTonesToOthersDialTones)
                        return ' ';
                    if (Column != 4) {
                        if (T[Column] / T[ii] < dialTonesToOthersDialTones)
                            return ' ';
                    } else {
                        if (T[Column] / T[ii] < (dialTonesToOthersDialTones / 3))
                            return ' ';
                    }
                }
            }
        }

        switch (Row) {
            case 0:
                switch (Column) {
                    case 4:
                        return_value = '1';
                        break;
                    case 5:
                        return_value = '2';
                        break;
                    case 6:
                        return_value = '3';
                        break;
                    case 7:
                        return_value = 'A';
                        break;
                }
                break;
            case 1:
                switch (Column) {
                    case 4:
                        return_value = '4';
                        break;
                    case 5:
                        return_value = '5';
                        break;
                    case 6:
                        return_value = '6';
                        break;
                    case 7:
                        return_value = 'B';
                        break;
                }
                break;
            case 2:
                switch (Column) {
                    case 4:
                        return_value = '7';
                        break;
                    case 5:
                        return_value = '8';
                        break;
                    case 6:
                        return_value = '9';
                        break;
                    case 7:
                        return_value = 'C';
                        break;
                }
                break;
            case 3:
                switch (Column) {
                    case 4:
                        return_value = '*';
                        break;
                    case 5:
                        return_value = '0';
                        break;
                    case 6:
                        return_value = '#';
                        break;
                    case 7:
                        return_value = 'D';
                        break;
                }
        }

        return return_value;
    }

    public List<Character> dialedButtons() {
        return dialledButtons;
    }

    public void detect() {
        short input_frame[] = sampleProvider.read();

        int ii;
        char temp_dial_button;

        for (ii = 0; ii < detectionSpec.frameSize(); ii++) {
            pArraySamples[ii + frame_count] = input_frame[ii];
        }

        frame_count += detectionSpec.frameSize();
        int temp_index = 0;
        if (frame_count >= SAMPLES) {
            while (frame_count >= SAMPLES) {
                if (temp_index == 0) {
                    temp_dial_button = dtmfDetection(pArraySamples);
                } else {
                    short tempArray[] = new short[pArraySamples.length - temp_index];
                    for (int inc = 0; inc < pArraySamples.length - temp_index; ++inc) {
                        tempArray[inc] = pArraySamples[temp_index + inc];
                    }
                    temp_dial_button = dtmfDetection(tempArray);
                }

                if (permissionFlag) {
                    if (temp_dial_button != ' ') {
                        dialledButtons.add(indexForDialButtons++, temp_dial_button);
                        dialledButtons.add(indexForDialButtons, (char) 0);
                        if (indexForDialButtons >= detectionSpec.maxButtonExpected())
                            throw new DTMFDetectionException("Too many tones detected");
                    }
                    permissionFlag = false;
                }

                if ((temp_dial_button != ' ') && (prevDialButton == ' ')) {
                    permissionFlag = true;
                }

                prevDialButton = temp_dial_button;

                temp_index += SAMPLES;
                frame_count -= SAMPLES;
            }

            for (ii = 0; ii < frame_count; ii++) {
                pArraySamples[ii] = pArraySamples[ii + temp_index];
            }
        }
    }

    private int mpy48sr(short o16, int o32) {
        int Temp0;
        int Temp1;
        Temp0 = (((char) o32 * o16) + 0x4000) >> 15;
        Temp1 = (short) (o32 >> 16) * o16;
        return (Temp1 << 1) + Temp0;
    }

    private void goertzelFilter(short koeff0, short koeff1, short arraySamples[], int Magnitude[], int COUNT, int index) {
        int Temp0, Temp1;
        short ii;
        int Vk1_0 = 0, Vk2_0 = 0, Vk1_1 = 0, Vk2_1 = 0;

        for (ii = 0; ii < COUNT; ++ii) {
            Temp0 = mpy48sr(koeff0, Vk1_0 << 1) - Vk2_0 + arraySamples[ii];
            Temp1 = mpy48sr(koeff1, Vk1_1 << 1) - Vk2_1 + arraySamples[ii];

            Vk2_0 = Vk1_0;
            Vk2_1 = Vk1_1;
            Vk1_0 = Temp0;
            Vk1_1 = Temp1;
        }

        Vk1_0 >>= 10;
        Vk1_1 >>= 10;
        Vk2_0 >>= 10;
        Vk2_1 >>= 10;
        Temp0 = mpy48sr(koeff0, Vk1_0 << 1);
        Temp1 = mpy48sr(koeff1, Vk1_1 << 1);
        Temp0 = (short) Temp0 * (short) Vk2_0;
        Temp1 = (short) Temp1 * (short) Vk2_1;
        Temp0 = (short) Vk1_0 * (short) Vk1_0 + (short) Vk2_0 * (short) Vk2_0 - Temp0;
        Temp1 = (short) Vk1_1 * (short) Vk1_1 + (short) Vk2_1 * (short) Vk2_1 - Temp1;
        Magnitude[index] = Temp0;
        Magnitude[index + 1] = Temp1;
    }

    @Override
    public void run() {
    }
}
