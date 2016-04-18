package tcc.view;/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Carta {

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            0.890552f, 0.573229f, 0.000000f,
            0.888928f, 0.587302f, 0.002047f,
            0.890552f, 0.573229f, 0.002047f,
            0.888928f, 0.587302f, 0.000000f,
            0.884304f, 0.600225f, 0.002047f,
            0.888928f, 0.587302f, 0.002047f,
            0.888928f, 0.587302f, 0.000000f,
            0.884304f, 0.600225f, 0.000000f,
            0.877050f, 0.611629f, 0.002047f,
            0.884304f, 0.600225f, 0.002047f,
            0.884304f, 0.600225f, 0.000000f,
            0.877050f, 0.611629f, 0.000000f,
            0.867535f, 0.621144f, 0.002047f,
            0.877050f, 0.611629f, 0.002047f,
            0.877050f, 0.611629f, 0.000000f,
            0.877050f, 0.611629f, 0.000000f,
            0.867535f, 0.621144f, 0.000000f,
            0.856131f, 0.628398f, 0.002047f,
            0.867535f, 0.621144f, 0.002047f,
            0.867535f, 0.621144f, 0.000000f,
            0.856131f, 0.628398f, 0.000000f,
            0.843207f, 0.633023f, 0.002047f,
            0.856131f, 0.628398f, 0.002047f,
            0.856131f, 0.628398f, 0.000000f,
            0.843207f, 0.633023f, 0.000000f,
            0.829135f, 0.634646f, 0.002047f,
            0.843207f, 0.633023f, 0.002047f,
            0.843207f, 0.633023f, 0.000000f,
            0.829135f, 0.634646f, 0.000000f,
            -0.829135f, 0.634646f, 0.002047f,
            0.829135f, 0.634646f, 0.002047f,
            0.829135f, 0.634646f, 0.000000f,
            -0.829135f, 0.634646f, 0.000000f,
            -0.843207f, 0.633023f, 0.002047f,
            -0.829135f, 0.634646f, 0.002047f,
            -0.829135f, 0.634646f, 0.000000f,
            -0.843207f, 0.633023f, 0.000000f,
            -0.856131f, 0.628398f, 0.002047f,
            -0.843207f, 0.633023f, 0.002047f,
            -0.843207f, 0.633023f, 0.000000f,
            -0.856131f, 0.628398f, 0.000000f,
            -0.867535f, 0.621144f, 0.002047f,
            -0.856131f, 0.628398f, 0.002047f,
            -0.856131f, 0.628398f, 0.000000f,
            -0.867535f, 0.621144f, 0.000000f,
            -0.877050f, 0.611629f, 0.002047f,
            -0.867535f, 0.621144f, 0.002047f,
            -0.867535f, 0.621144f, 0.000000f,
            -0.877050f, 0.611629f, 0.000000f,
            -0.884304f, 0.600225f, 0.002047f,
            -0.877050f, 0.611629f, 0.002047f,
            -0.877050f, 0.611629f, 0.000000f,
            -0.884304f, 0.600225f, 0.000000f,
            -0.888928f, 0.587302f, 0.002047f,
            -0.884304f, 0.600225f, 0.002047f,
            -0.884304f, 0.600225f, 0.000000f,
            -0.884304f, 0.600225f, 0.000000f,
            -0.888928f, 0.587302f, 0.000000f,
            -0.890552f, 0.573229f, 0.002047f,
            -0.888928f, 0.587302f, 0.002047f,
            -0.888928f, 0.587302f, 0.000000f,
            -0.890552f, 0.573229f, 0.000000f,
            -0.890552f, -0.573229f, 0.002047f,
            -0.890552f, 0.573229f, 0.002047f,
            -0.890552f, 0.573229f, 0.000000f,
            -0.890552f, -0.573229f, 0.000000f,
            -0.888928f, -0.587302f, 0.002047f,
            -0.890552f, -0.573229f, 0.002047f,
            -0.890552f, -0.573229f, 0.000000f,
            -0.888928f, -0.587302f, 0.000000f,
            -0.884304f, -0.600225f, 0.002047f,
            -0.888928f, -0.587302f, 0.002047f,
            -0.888928f, -0.587302f, 0.000000f,
            -0.884304f, -0.600225f, 0.000000f,
            -0.877050f, -0.611629f, 0.002047f,
            -0.884304f, -0.600225f, 0.002047f,
            -0.884304f, -0.600225f, 0.000000f,
            -0.877050f, -0.611629f, 0.000000f,
            -0.867535f, -0.621144f, 0.002047f,
            -0.877050f, -0.611629f, 0.002047f,
            -0.877050f, -0.611629f, 0.000000f,
            -0.877050f, -0.611629f, 0.000000f,
            -0.867535f, -0.621144f, 0.000000f,
            -0.856131f, -0.628398f, 0.002047f,
            -0.867535f, -0.621144f, 0.002047f,
            -0.867535f, -0.621144f, 0.000000f,
            -0.856131f, -0.628398f, 0.000000f,
            -0.843207f, -0.633023f, 0.002047f,
            -0.856131f, -0.628398f, 0.002047f,
            -0.856131f, -0.628398f, 0.000000f,
            -0.843207f, -0.633023f, 0.000000f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.843207f, -0.633023f, 0.002047f,
            -0.843207f, -0.633023f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.829135f, -0.634646f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.829135f, -0.634646f, 0.000000f,
            0.829135f, -0.634646f, 0.000000f,
            0.843207f, -0.633023f, 0.002047f,
            0.829135f, -0.634646f, 0.002047f,
            0.829135f, -0.634646f, 0.000000f,
            0.843207f, -0.633023f, 0.000000f,
            0.856131f, -0.628398f, 0.002047f,
            0.843207f, -0.633023f, 0.002047f,
            0.843207f, -0.633023f, 0.000000f,
            0.856131f, -0.628398f, 0.000000f,
            0.867535f, -0.621144f, 0.002047f,
            0.856131f, -0.628398f, 0.002047f,
            0.856131f, -0.628398f, 0.000000f,
            0.867535f, -0.621144f, 0.000000f,
            0.877050f, -0.611629f, 0.002047f,
            0.867535f, -0.621144f, 0.002047f,
            0.867535f, -0.621144f, 0.000000f,
            0.877050f, -0.611629f, 0.000000f,
            0.884304f, -0.600225f, 0.002047f,
            0.877050f, -0.611629f, 0.002047f,
            0.877050f, -0.611629f, 0.000000f,
            0.884304f, -0.600225f, 0.000000f,
            0.888928f, -0.587302f, 0.002047f,
            0.884304f, -0.600225f, 0.002047f,
            0.884304f, -0.600225f, 0.000000f,
            0.884304f, -0.600225f, 0.000000f,
            0.888928f, -0.587302f, 0.000000f,
            0.890552f, -0.573229f, 0.002047f,
            0.888928f, -0.587302f, 0.002047f,
            0.888928f, -0.587302f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.890552f, 0.573229f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.890552f, -0.573229f, 0.000000f,
            0.890552f, 0.573229f, 0.000000f,
            -0.856131f, -0.628398f, 0.000000f,
            -0.867535f, -0.621144f, 0.000000f,
            -0.877050f, -0.611629f, 0.000000f,
            -0.856131f, -0.628398f, 0.000000f,
            -0.877050f, -0.611629f, 0.000000f,
            -0.884304f, -0.600225f, 0.000000f,
            -0.856131f, -0.628398f, 0.000000f,
            -0.884304f, -0.600225f, 0.000000f,
            -0.888928f, -0.587302f, 0.000000f,
            -0.843207f, -0.633023f, 0.000000f,
            -0.856131f, -0.628398f, 0.000000f,
            -0.888928f, -0.587302f, 0.000000f,
            -0.843207f, -0.633023f, 0.000000f,
            -0.888928f, -0.587302f, 0.000000f,
            -0.890552f, -0.573229f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.843207f, -0.633023f, 0.000000f,
            -0.890552f, -0.573229f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.890552f, -0.573229f, 0.000000f,
            -0.890552f, 0.573229f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.890552f, 0.573229f, 0.000000f,
            -0.888928f, 0.587302f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.888928f, 0.587302f, 0.000000f,
            -0.884304f, 0.600225f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.884304f, 0.600225f, 0.000000f,
            -0.877050f, 0.611629f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.877050f, 0.611629f, 0.000000f,
            -0.867535f, 0.621144f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.867535f, 0.621144f, 0.000000f,
            -0.856131f, 0.628398f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.856131f, 0.628398f, 0.000000f,
            -0.843207f, 0.633023f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.843207f, 0.633023f, 0.000000f,
            -0.829135f, 0.634646f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            -0.829135f, 0.634646f, 0.000000f,
            0.829135f, 0.634646f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.829135f, 0.634646f, 0.000000f,
            0.843207f, 0.633023f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.843207f, 0.633023f, 0.000000f,
            0.856131f, 0.628398f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.856131f, 0.628398f, 0.000000f,
            0.867535f, 0.621144f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.867535f, 0.621144f, 0.000000f,
            0.877050f, 0.611629f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.877050f, 0.611629f, 0.000000f,
            0.884304f, 0.600225f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.884304f, 0.600225f, 0.000000f,
            0.888928f, 0.587302f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.888928f, 0.587302f, 0.000000f,
            0.890552f, 0.573229f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.890552f, 0.573229f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.829135f, -0.634646f, 0.000000f,
            -0.829135f, -0.634646f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.843207f, -0.633023f, 0.000000f,
            0.829135f, -0.634646f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.856131f, -0.628398f, 0.000000f,
            0.843207f, -0.633023f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.867535f, -0.621144f, 0.000000f,
            0.856131f, -0.628398f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.877050f, -0.611629f, 0.000000f,
            0.867535f, -0.621144f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.884304f, -0.600225f, 0.000000f,
            0.877050f, -0.611629f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.884304f, -0.600225f, 0.000000f,
            0.890552f, -0.573229f, 0.000000f,
            0.888928f, -0.587302f, 0.000000f,
            0.884304f, -0.600225f, 0.002047f,
            0.888928f, -0.587302f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            -0.877050f, -0.611629f, 0.002047f,
            -0.867535f, -0.621144f, 0.002047f,
            -0.856131f, -0.628398f, 0.002047f,
            -0.884304f, -0.600225f, 0.002047f,
            -0.877050f, -0.611629f, 0.002047f,
            -0.856131f, -0.628398f, 0.002047f,
            -0.888928f, -0.587302f, 0.002047f,
            -0.884304f, -0.600225f, 0.002047f,
            -0.856131f, -0.628398f, 0.002047f,
            -0.888928f, -0.587302f, 0.002047f,
            -0.856131f, -0.628398f, 0.002047f,
            -0.843207f, -0.633023f, 0.002047f,
            -0.890552f, -0.573229f, 0.002047f,
            -0.888928f, -0.587302f, 0.002047f,
            -0.843207f, -0.633023f, 0.002047f,
            -0.890552f, -0.573229f, 0.002047f,
            -0.843207f, -0.633023f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.890552f, 0.573229f, 0.002047f,
            -0.890552f, -0.573229f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.888928f, 0.587302f, 0.002047f,
            -0.890552f, 0.573229f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.884304f, 0.600225f, 0.002047f,
            -0.888928f, 0.587302f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.877050f, 0.611629f, 0.002047f,
            -0.884304f, 0.600225f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.867535f, 0.621144f, 0.002047f,
            -0.877050f, 0.611629f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.856131f, 0.628398f, 0.002047f,
            -0.867535f, 0.621144f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.843207f, 0.633023f, 0.002047f,
            -0.856131f, 0.628398f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            -0.829135f, 0.634646f, 0.002047f,
            -0.843207f, 0.633023f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.829135f, 0.634646f, 0.002047f,
            -0.829135f, 0.634646f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.843207f, 0.633023f, 0.002047f,
            0.829135f, 0.634646f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.856131f, 0.628398f, 0.002047f,
            0.843207f, 0.633023f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.867535f, 0.621144f, 0.002047f,
            0.856131f, 0.628398f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.877050f, 0.611629f, 0.002047f,
            0.867535f, 0.621144f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.884304f, 0.600225f, 0.002047f,
            0.877050f, 0.611629f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.888928f, 0.587302f, 0.002047f,
            0.884304f, 0.600225f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.890552f, 0.573229f, 0.002047f,
            0.888928f, 0.587302f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.890552f, 0.573229f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            -0.829135f, -0.634646f, 0.002047f,
            0.829135f, -0.634646f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.829135f, -0.634646f, 0.002047f,
            0.843207f, -0.633023f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.843207f, -0.633023f, 0.002047f,
            0.856131f, -0.628398f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.856131f, -0.628398f, 0.002047f,
            0.867535f, -0.621144f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.867535f, -0.621144f, 0.002047f,
            0.877050f, -0.611629f, 0.002047f,
            0.884304f, -0.600225f, 0.002047f,
            0.890552f, -0.573229f, 0.002047f,
            0.877050f, -0.611629f, 0.002047f};

    private final short drawOrder[] = {
            3, 0, 1, 2,
            3, 0, 3, 1,
            3, 4, 5, 6,
            3, 6, 7, 4,
            3, 8, 9, 10,
            3, 10, 11, 8,
            3, 12, 13, 14,
            3, 15, 16, 12,
            3, 17, 18, 19,
            3, 19, 20, 17,
            3, 21, 22, 23,
            3, 23, 24, 21,
            3, 25, 26, 27,
            3, 27, 28, 25,
            3, 29, 30, 31,
            3, 31, 32, 29,
            3, 33, 34, 35,
            3, 35, 36, 33,
            3, 37, 38, 39,
            3, 39, 40, 37,
            3, 41, 42, 43,
            3, 43, 44, 41,
            3, 45, 46, 47,
            3, 47, 48, 45,
            3, 49, 50, 51,
            3, 51, 52, 49,
            3, 53, 54, 55,
            3, 56, 57, 53,
            3, 58, 59, 60,
            3, 60, 61, 58,
            3, 62, 63, 64,
            3, 64, 65, 62,
            3, 66, 67, 68,
            3, 68, 69, 66,
            3, 70, 71, 72,
            3, 72, 73, 70,
            3, 74, 75, 76,
            3, 76, 77, 74,
            3, 78, 79, 80,
            3, 81, 82, 78,
            3, 83, 84, 85,
            3, 85, 86, 83,
            3, 87, 88, 89,
            3, 89, 90, 87,
            3, 91, 92, 93,
            3, 93, 94, 91,
            3, 95, 96, 97,
            3, 97, 98, 95,
            3, 99, 100, 101,
            3, 101, 102, 99,
            3, 103, 104, 105,
            3, 105, 106, 103,
            3, 107, 108, 109,
            3, 109, 110, 107,
            3, 111, 112, 113,
            3, 113, 114, 111,
            3, 115, 116, 117,
            3, 117, 118, 115,
            3, 119, 120, 121,
            3, 122, 123, 119,
            3, 124, 125, 126,
            3, 126, 127, 124,
            3, 128, 129, 130,
            3, 130, 131, 128,
            3, 132, 133, 134,
            3, 135, 136, 137,
            3, 138, 139, 140,
            3, 141, 142, 143,
            3, 144, 145, 146,
            3, 147, 148, 149,
            3, 150, 151, 152,
            3, 153, 154, 155,
            3, 156, 157, 158,
            3, 159, 160, 161,
            3, 162, 163, 164,
            3, 165, 166, 167,
            3, 168, 169, 170,
            3, 171, 172, 173,
            3, 174, 175, 176,
            3, 177, 178, 179,
            3, 180, 181, 182,
            3, 183, 184, 185,
            3, 186, 187, 188,
            3, 189, 190, 191,
            3, 192, 193, 194,
            3, 195, 196, 197,
            3, 198, 199, 200,
            3, 201, 202, 203,
            3, 204, 205, 206,
            3, 207, 208, 209,
            3, 210, 211, 212,
            3, 213, 214, 215,
            3, 216, 217, 218,
            3, 219, 220, 221,
            3, 222, 223, 224,
            3, 225, 226, 227,
            3, 228, 229, 230,
            3, 231, 232, 233,
            3, 234, 235, 236,
            3, 237, 238, 239,
            3, 240, 241, 242,
            3, 243, 244, 245,
            3, 246, 247, 248,
            3, 249, 250, 251,
            3, 252, 253, 254,
            3, 255, 256, 257,
            3, 258, 259, 260,
            3, 261, 262, 263,
            3, 264, 265, 266,
            3, 267, 268, 269,
            3, 270, 271, 272,
            3, 273, 274, 275,
            3, 276, 277, 278,
            3, 279, 280, 281,
            3, 282, 283, 284,
            3, 285, 286, 287,
            3, 288, 289, 290,
            3, 291, 292, 293,
            3, 294, 295, 296,
            3, 297, 298, 299,
            3, 300, 301, 302,
            3, 303, 304, 305,
            3, 306, 307, 308,
            3, 309, 310, 311}; // order to draw vertices

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    final int textureHandle;

    HashMap<String, float[]> imagemCarta = new HashMap<>();

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Carta(Bitmap bitmap) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        String vertexShaderCode = "uniform mat4 uMVPMatrix;" +
                "uniform mat4 verTexTrasf;" +
                "uniform mat4 u_tctMatrix;" +
                "attribute vec4 vPosition;" +
                "varying vec2 vTexCoord;" +
                "void main() {" +
                "  vTexCoord = (u_tctMatrix * verTexTrasf * vPosition).xy;" +
                // The matrix must be included as a modifier of gl_Position.
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}";
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        String fragmentShaderCode = "precision mediump float;" +
                "uniform vec4 vColor;" +
                "uniform sampler2D u_texture;" +
                "varying vec2 vTexCoord;" +
                "void main() {" +
                "  gl_FragColor = texture2D(u_texture, vTexCoord);" +
                "}";
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

        // preparando a textura
        int[] memTextureHandle = new int[1];

        GLES20.glGenTextures(1, memTextureHandle, 0);

        if (memTextureHandle[0] != 0){
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, memTextureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (memTextureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }
        textureHandle = memTextureHandle[0];

        imagemCarta.put("Joker Black", new float[] {0f, 0f});
        imagemCarta.put("As", new float[] {1f, 0f});
        imagemCarta.put("Ah", new float[] {2f, 0f});
        imagemCarta.put("Ad", new float[] {3f, 0f});
        imagemCarta.put("Ac", new float[] {4f, 0f});
        imagemCarta.put("Joker Red", new float[] {0f, 1f});
        imagemCarta.put("2s", new float[] {1f, 1f});
        imagemCarta.put("2h", new float[] {2f, 1f});
        imagemCarta.put("2d", new float[] {3f, 1f});
        imagemCarta.put("2c", new float[] {4f, 1f});
        imagemCarta.put("Back", new float[] {0f, 2f});
        imagemCarta.put("3s", new float[] {1f, 2f});
        imagemCarta.put("3h", new float[] {2f, 2f});
        imagemCarta.put("3d", new float[] {3f, 2f});
        imagemCarta.put("3c", new float[] {4f, 2f});
        imagemCarta.put("4s", new float[] {1f, 3f});
        imagemCarta.put("4h", new float[] {2f, 3f});
        imagemCarta.put("4d", new float[] {3f, 3f});
        imagemCarta.put("4c", new float[] {4f, 3f});
        imagemCarta.put("5s", new float[] {1f, 4f});
        imagemCarta.put("5h", new float[] {2f, 4f});
        imagemCarta.put("5d", new float[] {3f, 4f});
        imagemCarta.put("5c", new float[] {4f, 4f});
        imagemCarta.put("6s", new float[] {1f, 5f});
        imagemCarta.put("6h", new float[] {2f, 5f});
        imagemCarta.put("6d", new float[] {3f, 5f});
        imagemCarta.put("6c", new float[] {4f, 5f});
        imagemCarta.put("7s", new float[] {1f, 6f});
        imagemCarta.put("7h", new float[] {2f, 6f});
        imagemCarta.put("7d", new float[] {3f, 6f});
        imagemCarta.put("7c", new float[] {4f, 6f});
        imagemCarta.put("8s", new float[] {1f, 7f});
        imagemCarta.put("8h", new float[] {2f, 7f});
        imagemCarta.put("8d", new float[] {3f, 7f});
        imagemCarta.put("8c", new float[] {4f, 7f});
        imagemCarta.put("9s", new float[] {1f, 8f});
        imagemCarta.put("9h", new float[] {2f, 8f});
        imagemCarta.put("9d", new float[] {3f, 8f});
        imagemCarta.put("9c", new float[] {4f, 8f});
        imagemCarta.put("Ts", new float[] {1f, 9f});
        imagemCarta.put("Th", new float[] {2f, 9f});
        imagemCarta.put("Td", new float[] {3f, 9f});
        imagemCarta.put("Tc", new float[] {4f, 9f});
        imagemCarta.put("Js", new float[] {1f, 10f});
        imagemCarta.put("Jh", new float[] {2f, 10f});
        imagemCarta.put("Jd", new float[] {3f, 10f});
        imagemCarta.put("Jc", new float[] {4f, 10f});
        imagemCarta.put("Qs", new float[] {1f, 11f});
        imagemCarta.put("Qh", new float[] {2f, 11f});
        imagemCarta.put("Qd", new float[] {3f, 11f});
        imagemCarta.put("Qc", new float[] {4f, 11f});
        imagemCarta.put("Ks", new float[] {1f, 12f});
        imagemCarta.put("Kh", new float[] {2f, 12f});
        imagemCarta.put("Kd", new float[] {3f, 12f});
        imagemCarta.put("Kc", new float[] {4f, 12f});
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void draw(float[] mvpMatrix, String card) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        int vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        MyGLRenderer.checkGlError("glActiveTexture");

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        MyGLRenderer.checkGlError("glBindTexture");

        int u_texture = GLES20.glGetUniformLocation(mProgram, "u_texture");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        GLES20.glUniform1i(u_texture, 0);
        MyGLRenderer.checkGlError("glUniform1i");

        // Calcular a matriz que transforma a coordenada de um vértice em coordenada de textura
        // Pegando a maior e a menor coordenada no eixo x do modelo da carta
        float xMax = Float.NEGATIVE_INFINITY;
        float xMin = Float.POSITIVE_INFINITY;
        for (int x = 0; x < squareCoords.length; x += 3) {
            if (xMax < squareCoords[x])
                xMax = squareCoords[x];
            if (xMin > squareCoords[x])
                xMin = squareCoords[x];
        }
        // Pegando a maior e a menor coordenada no eixo y do modelo da carta
        float yMax = Float.NEGATIVE_INFINITY;
        float yMin = Float.POSITIVE_INFINITY;
        for (int y = 1; y < squareCoords.length; y += 3) {
            if (yMax < squareCoords[y])
                yMax = squareCoords[y];
            if (yMin > squareCoords[y])
                yMin = squareCoords[y];
        }

        float[] vttMatrix = new float[16];
        Matrix.setIdentityM(vttMatrix, 0);
        Matrix.scaleM(vttMatrix, 0, vttMatrix, 0, 1f / (xMax - xMin), 1f / (yMax - yMin), 1f);
        Matrix.translateM(vttMatrix, 0, vttMatrix, 0, -xMin, -yMin, 0f);

        int verTexTrasf = GLES20.glGetUniformLocation(mProgram, "verTexTrasf");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Aplicando a transformação da coordenadas de um vértice para as coordenadas de textura
        GLES20.glUniformMatrix4fv(verTexTrasf, 1, false, vttMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Transformando a coordenada de textura para a coordenada de uma carta
        float[] tctMatrix = new float[16];
        Matrix.setIdentityM(tctMatrix, 0);
        Matrix.scaleM(tctMatrix, 0, tctMatrix, 0, 0.1998355263f, 0.0769158494f, 1f);
        Matrix.translateM(tctMatrix, 0, tctMatrix, 0, imagemCarta.get(card)[0], imagemCarta.get(card)[1], 0f);

        int u_tctMatrix = GLES20.glGetUniformLocation(mProgram, "u_tctMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Aplicando a transformação da coordenadas de um vértice para as coordenadas de textura
        GLES20.glUniformMatrix4fv(u_tctMatrix, 1, false, tctMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}