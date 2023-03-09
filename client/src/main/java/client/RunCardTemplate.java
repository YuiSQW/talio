/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;

import java.util.Objects;


public class RunCardTemplate extends Application {


    public static void main(String[] args)  {
        launch();

    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        var xml = getClass().getClassLoader().getResource("client/scenes/CardOverview.fxml");
        Parent root = FXMLLoader.load((Objects.requireNonNull(xml)));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();


    }


}