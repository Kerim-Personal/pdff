package com.example.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewCourses: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val courseList = mutableListOf<Course>()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
        applyTheme()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIFeedbackHelper.init(this)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.topToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getGreetingMessage(this)

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses)
        setupRecyclerView()
        loadCourses()
    }

    private fun getGreetingMessage(context: Context): String {
        val name = SharedPreferencesManager.getUserName(context)
        if (name.isNullOrEmpty()) {
            return getString(R.string.app_name)
        }

        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> getString(R.string.greeting_good_morning, name)
            in 12..17 -> getString(R.string.greeting_good_day, name)
            in 18..21 -> getString(R.string.greeting_good_evening, name)
            else -> getString(R.string.greeting_good_night, name)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UIFeedbackHelper.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val settingsItem = menu?.findItem(R.id.action_settings)
        settingsItem?.icon?.let { icon ->
            val newIcon = icon.mutate()
            val goldColor = ContextCompat.getColor(this, R.color.gold_accent)
            newIcon.setTint(goldColor)
            settingsItem.icon = newIcon
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                UIFeedbackHelper.provideFeedback(findViewById(R.id.action_settings))
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        courseAdapter = CourseAdapter(
            this,
            courseList,
            onTopicClickListener = { courseTitle, topicTitle ->
                Toast.makeText(this, getString(R.string.topic_pdf_not_found, courseTitle, topicTitle), Toast.LENGTH_SHORT).show()
            },
            onPdfClickListener = { courseTitle, topicTitle, pdfAssetName ->
                val intent = Intent(this, PdfViewActivity::class.java)
                intent.putExtra(PdfViewActivity.EXTRA_PDF_ASSET_NAME, pdfAssetName)
                intent.putExtra(PdfViewActivity.EXTRA_PDF_TITLE, "$courseTitle - $topicTitle")
                startActivity(intent)
            }
        )
        recyclerViewCourses.layoutManager = LinearLayoutManager(this)
        recyclerViewCourses.adapter = courseAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadCourses() {
        courseList.clear()

        // --- TÜM DERSLER ARTIK STRİNG KAYNAKLARINDAN YÜKLENİYOR ---

        courseList.add(Course(getString(R.string.course_calculus), listOf(
            getString(R.string.topic_calculus_limit), getString(R.string.topic_calculus_derivative_rules), getString(R.string.topic_calculus_derivative_apps),
            getString(R.string.topic_calculus_mean_value), getString(R.string.topic_calculus_indefinite_integral), getString(R.string.topic_calculus_definite_integral),
            getString(R.string.topic_calculus_sequences_series), getString(R.string.topic_calculus_power_series), getString(R.string.topic_calculus_multivariable),
            getString(R.string.topic_calculus_partial_derivatives), getString(R.string.topic_calculus_multiple_integrals), getString(R.string.topic_calculus_vector_analysis),
            getString(R.string.topic_calculus_fourier), getString(R.string.topic_calculus_metric_spaces)
        )))

        courseList.add(Course(getString(R.string.course_complex_analysis), listOf(
            getString(R.string.topic_complex_numbers), getString(R.string.topic_complex_analytic_functions), getString(R.string.topic_complex_cauchy_riemann),
            getString(R.string.topic_complex_integration), getString(R.string.topic_complex_cauchy_integral), getString(R.string.topic_complex_taylor_laurent),
            getString(R.string.topic_complex_residue), getString(R.string.topic_complex_conformal), getString(R.string.topic_complex_dynamic_systems)
        )))

        courseList.add(Course(getString(R.string.course_numerical_analysis), listOf(
            getString(R.string.topic_numerical_error_analysis), getString(R.string.topic_numerical_root_finding), getString(R.string.topic_numerical_linear_systems),
            getString(R.string.topic_numerical_iterative_methods), getString(R.string.topic_numerical_interpolation), getString(R.string.topic_numerical_least_squares),
            getString(R.string.topic_numerical_differentiation_integration), getString(R.string.topic_numerical_matrix_decomposition), getString(R.string.topic_numerical_optimization)
        )))

        courseList.add(Course(getString(R.string.course_linear_algebra), listOf(
            getString(R.string.topic_linear_matrices), getString(R.string.topic_linear_systems), getString(R.string.topic_linear_vector_spaces),
            getString(R.string.topic_linear_independence), getString(R.string.topic_linear_transformations), getString(R.string.topic_linear_kernel_image),
            getString(R.string.topic_linear_eigenvalues), getString(R.string.topic_linear_diagonalization), getString(R.string.topic_linear_inner_product),
            getString(R.string.topic_linear_tensors), getString(R.string.topic_linear_numerical)
        )))

        courseList.add(Course(getString(R.string.course_abstract_math), listOf(
            getString(R.string.topic_abstract_logic), getString(R.string.topic_abstract_set_theory), getString(R.string.topic_abstract_proof_methods),
            getString(R.string.topic_abstract_relations), getString(R.string.topic_abstract_functions), getString(R.string.topic_abstract_cardinality),
            getString(R.string.topic_abstract_category_theory), getString(R.string.topic_abstract_model_theory)
        )))

        courseList.add(Course(getString(R.string.course_algebra), listOf(
            getString(R.string.topic_algebra_groups), getString(R.string.topic_algebra_cyclic_groups), getString(R.string.topic_algebra_lagrange),
            getString(R.string.topic_algebra_normal_subgroups), getString(R.string.topic_algebra_homomorphisms), getString(R.string.topic_algebra_rings_fields),
            getString(R.string.topic_algebra_module_theory)
        )))

        courseList.add(Course(getString(R.string.course_number_theory), listOf(
            getString(R.string.topic_number_divisibility), getString(R.string.topic_number_prime_numbers), getString(R.string.topic_number_modular_arithmetic),
            getString(R.string.topic_number_chinese_remainder), getString(R.string.topic_number_fermat_euler), getString(R.string.topic_number_diophantine),
            getString(R.string.topic_number_cryptography), getString(R.string.topic_number_quadratic_residues), getString(R.string.topic_number_analytic_number_theory)
        )))

        courseList.add(Course(getString(R.string.course_differential_equations), listOf(
            getString(R.string.topic_diffeq_first_order), getString(R.string.topic_diffeq_higher_order), getString(R.string.topic_diffeq_undetermined_coefficients),
            getString(R.string.topic_diffeq_variation_parameters), getString(R.string.topic_diffeq_laplace), getString(R.string.topic_diffeq_initial_value),
            getString(R.string.topic_diffeq_systems), getString(R.string.topic_diffeq_chaotic_systems), getString(R.string.topic_diffeq_numerical_solutions)
        )))

        courseList.add(Course(getString(R.string.course_pde), listOf(
            getString(R.string.topic_pde_concepts), getString(R.string.topic_pde_fourier), getString(R.string.topic_pde_separation_variables),
            getString(R.string.topic_pde_heat_equation), getString(R.string.topic_pde_wave_equation), getString(R.string.topic_pde_laplace_equation),
            getString(R.string.topic_pde_green_functions), getString(R.string.topic_pde_finite_element)
        )))

        courseList.add(Course(getString(R.string.course_analytic_geometry), listOf(
            getString(R.string.topic_analytic_geometry_coordinates), getString(R.string.topic_analytic_geometry_line_plane), getString(R.string.topic_analytic_geometry_conic_sections),
            getString(R.string.topic_analytic_geometry_quadric_surfaces), getString(R.string.topic_analytic_geometry_transformations), getString(R.string.topic_analytic_geometry_projective),
            getString(R.string.topic_analytic_geometry_vector_fields)
        )))

        courseList.add(Course(getString(R.string.course_differential_geometry), listOf(
            getString(R.string.topic_diffgeo_curves), getString(R.string.topic_diffgeo_frenet_serret), getString(R.string.topic_diffgeo_surfaces),
            getString(R.string.topic_diffgeo_fundamental_forms), getString(R.string.topic_diffgeo_gaussian_curvature), getString(R.string.topic_diffgeo_gauss_bonnet),
            getString(R.string.topic_diffgeo_riemann_geometry), getString(R.string.topic_diffgeo_manifolds)
        )))

        courseList.add(Course(getString(R.string.course_topology), listOf(
            getString(R.string.topic_topology_spaces), getString(R.string.topic_topology_metric_spaces), getString(R.string.topic_topology_continuity),
            getString(R.string.topic_topology_connectedness), getString(R.string.topic_topology_compactness), getString(R.string.topic_topology_fundamental_group),
            getString(R.string.topic_topology_knot_theory), getString(R.string.topic_topology_homology_cohomology)
        )))

        courseList.add(Course(getString(R.string.course_probability), listOf(
            getString(R.string.topic_probability_counting), getString(R.string.topic_probability_conditional), getString(R.string.topic_probability_random_variables),
            getString(R.string.topic_probability_discrete_distributions), getString(R.string.topic_probability_continuous_distributions), getString(R.string.topic_probability_expected_value),
            getString(R.string.topic_probability_limit_theorems), getString(R.string.topic_probability_markov_chains), getString(R.string.topic_probability_statistical_inference)
        )))

        courseList.add(Course(getString(R.string.course_functional_analysis), listOf(
            getString(R.string.topic_functional_normed_spaces), getString(R.string.topic_functional_hilbert_spaces), getString(R.string.topic_functional_linear_operators),
            getString(R.string.topic_functional_spectral_theory), getString(R.string.topic_functional_weak_topologies), getString(R.string.topic_functional_applications)
        )))

        courseAdapter.notifyDataSetChanged()
    }

    private fun applyTheme() {
        val theme = SharedPreferencesManager.getTheme(this)
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}