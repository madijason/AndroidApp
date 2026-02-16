package com.financetracker.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.financetracker.app.ui.ProjectsViewModel

@Composable
fun MainNavigation(viewModel: ProjectsViewModel) {
    val navController = rememberNavController()
    val selectedProject by viewModel.selectedProject.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onProjectClick = { project ->
                    viewModel.selectProject(project)
                    navController.navigate("projectDetail")
                }
            )
        }
        
        composable("projectDetail") {
            selectedProject?.let { project ->
                ProjectDetailScreen(
                    project = project,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
