
const API = "http://localhost:8081/api";

let patients = [];
let doctors = [];
let tokens = [];

// ---------------- NAVIGATION ----------------

function showSection(sectionId, button) {
    document.querySelectorAll(".section").forEach(section => {
        section.classList.add("hidden");
    });

    document.getElementById(sectionId).classList.remove("hidden");

    document.querySelectorAll(".tab").forEach(tab => {
        tab.classList.remove("active");
    });

    if (button) {
        button.classList.add("active");
    }

    if (sectionId === "dashboard") loadDashboard();
    if (sectionId === "patients") loadPatients();
    if (sectionId === "doctors") loadDoctors();

    if (sectionId === "tokens") {
        loadPatients();
        loadDoctors();
        loadTokens();
    }
}

// ---------------- TOAST ----------------

function showToast(message, isError = false) {
    const toast = document.getElementById("toast");

    toast.textContent = message;
    toast.className = isError ? "toast error" : "toast";
    toast.style.display = "block";

    setTimeout(() => {
        toast.style.display = "none";
    }, 3000);
}

// ---------------- API HELPER ----------------

async function apiRequest(url, method = "GET", data = null) {
    const options = {
        method: method,
        headers: {
            "Content-Type": "application/json"
        }
    };

    if (data !== null) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);
    const text = await response.text();

    if (!response.ok) {
        throw new Error(text || `HTTP Error: ${response.status}`);
    }

    if (!text) return null;

    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

// ---------------- HELPER FUNCTIONS ----------------

function getId(item) {
    return item.id ?? item.Id;
}

function getValue(item, lower, upper) {
    return item[lower] ?? item[upper];
}

function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}

function getPatientName(token) {
    const patient = getValue(token, "patient", "Patient");

    return patient
        ? getValue(patient, "patientName", "PatientName")
        : "-";
}

function getDoctorName(token) {
    const doctor = getValue(token, "doctor", "Doctor");

    return doctor
        ? getValue(doctor, "doctorName", "DoctorName")
        : "-";
}

// ==================================================
// PATIENT CRUD
// ==================================================

async function loadPatients() {
    try {
        const data = await apiRequest(`${API}/patient/getall`);

        patients = Array.isArray(data) ? data : [];

        const table = document.getElementById("patientTable");

        if (table) {
            if (patients.length === 0) {
                table.innerHTML =
                    '<tr><td colspan="6">No patients found</td></tr>';
            } else {
                table.innerHTML = patients.map(patient => `
                    <tr>
                        <td>${escapeHTML(getId(patient))}</td>
                        <td>${escapeHTML(getValue(patient, "patientName", "PatientName"))}</td>
                        <td>${escapeHTML(getValue(patient, "age", "Age"))}</td>
                        <td>${escapeHTML(getValue(patient, "gender", "Gender"))}</td>
                        <td>${escapeHTML(getValue(patient, "contactNumber", "ContactNumber"))}</td>
                        <td>
                            <button class="action-btn edit-btn"
                                onclick="editPatient(${getId(patient)})">
                                Edit
                            </button>

                            <button class="action-btn delete-btn"
                                onclick="deletePatient(${getId(patient)})">
                                Delete
                            </button>
                        </td>
                    </tr>
                `).join("");
            }
        }

        updatePatientSelect();

        document.getElementById("patientCount").textContent =
            patients.length;

    } catch (error) {
        console.error("Patient loading error:", error);
        showToast("Unable to load patients", true);
    }
}

// CREATE AND UPDATE PATIENT

document.getElementById("patientForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("patientId").value;

    const patient = {
        patientName: document.getElementById("patientName").value.trim(),
        age: Number(document.getElementById("patientAge").value),
        gender: document.getElementById("patientGender").value,
        contactNumber: document.getElementById("patientContact").value.trim()
    };

    try {
        if (id) {
            // Backend expects PUT /update, not /update/{id}
            const updatedPatient = {
                id: Number(id),
                ...patient
            };

            await apiRequest(
                `${API}/patient/update`,
                "PUT",
                updatedPatient
            );

            showToast("Patient updated successfully");

        } else {
            await apiRequest(
                `${API}/patient/create`,
                "POST",
                patient
            );

            showToast("Patient added successfully");
        }

        resetPatientForm();

        await loadPatients();
        await loadDashboard();

    } catch (error) {
        console.error("Patient save error:", error);
        showToast(error.message || "Patient operation failed", true);
    }
});

// EDIT PATIENT

function editPatient(id) {
    const patient = patients.find(
        item => Number(getId(item)) === Number(id)
    );

    if (!patient) {
        showToast("Patient not found", true);
        return;
    }

    document.getElementById("patientId").value = getId(patient);

    document.getElementById("patientName").value =
        getValue(patient, "patientName", "PatientName") ?? "";

    document.getElementById("patientAge").value =
        getValue(patient, "age", "Age") ?? "";

    document.getElementById("patientGender").value =
        getValue(patient, "gender", "Gender") ?? "";

    document.getElementById("patientContact").value =
        getValue(patient, "contactNumber", "ContactNumber") ?? "";

    document.getElementById("patientFormTitle").textContent =
        "Update Patient";

    document.getElementById("patientSubmitBtn").textContent =
        "Update Patient";

    showSection("patients", document.querySelectorAll(".tab")[1]);

    window.scrollTo({ top: 0, behavior: "smooth" });
}

// DELETE PATIENT

async function deletePatient(id) {
    if (!confirm("Are you sure you want to delete this patient?")) {
        return;
    }

    try {
        await apiRequest(
            `${API}/patient/delete/${id}`,
            "DELETE"
        );

        showToast("Patient deleted successfully");

        await loadPatients();
        await loadDashboard();

    } catch (error) {
        console.error("Patient delete error:", error);
        showToast(error.message || "Unable to delete patient", true);
    }
}

// RESET PATIENT FORM

function resetPatientForm() {
    document.getElementById("patientForm").reset();

    document.getElementById("patientId").value = "";

    document.getElementById("patientFormTitle").textContent =
        "Add Patient";

    document.getElementById("patientSubmitBtn").textContent =
        "Add Patient";
}

// ==================================================
// DOCTOR CRUD
// ==================================================

async function loadDoctors() {
    try {
        const data = await apiRequest(`${API}/doctor/getall`);

        doctors = Array.isArray(data) ? data : [];

        const table = document.getElementById("doctorTable");

        if (table) {
            if (doctors.length === 0) {
                table.innerHTML =
                    '<tr><td colspan="6">No doctors found</td></tr>';
            } else {
                table.innerHTML = doctors.map(doctor => `
                    <tr>
                        <td>${escapeHTML(getId(doctor))}</td>
                        <td>${escapeHTML(getValue(doctor, "doctorName", "DoctorName"))}</td>
                        <td>${escapeHTML(getValue(doctor, "specialization", "Specialization"))}</td>
                        <td>${escapeHTML(getValue(doctor, "averageConsultationTime", "AverageConsultationTime"))}</td>
                        <td>${escapeHTML(getValue(doctor, "currentServingToken", "CurrentServingToken"))}</td>
                        <td>
                            <button class="action-btn edit-btn"
                                onclick="editDoctor(${getId(doctor)})">
                                Edit
                            </button>

                            <button class="action-btn delete-btn"
                                onclick="deleteDoctor(${getId(doctor)})">
                                Delete
                            </button>
                        </td>
                    </tr>
                `).join("");
            }
        }

        updateDoctorSelect();

        document.getElementById("doctorCount").textContent =
            doctors.length;

    } catch (error) {
        console.error("Doctor loading error:", error);
        showToast("Unable to load doctors", true);
    }
}

// CREATE AND UPDATE DOCTOR

document.getElementById("doctorForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const id = document.getElementById("doctorId").value;

    const doctor = {
        doctorName: document.getElementById("doctorName").value.trim(),
        specialization: document.getElementById("doctorSpecialization").value.trim(),
        averageConsultationTime: Number(document.getElementById("doctorTime").value),
        currentServingToken: Number(document.getElementById("doctorCurrentToken").value)
    };

    try {
        if (id) {
            await apiRequest(
                `${API}/doctor/update`,
                "PUT",
                {
                    id: Number(id),
                    ...doctor
                }
            );

            showToast("Doctor updated successfully");

        } else {
            await apiRequest(
                `${API}/doctor/create`,
                "POST",
                doctor
            );

            showToast("Doctor added successfully");
        }

        resetDoctorForm();

        await loadDoctors();
        await loadDashboard();

    } catch (error) {
        console.error("Doctor save error:", error);
        showToast(error.message || "Doctor operation failed", true);
    }
});

// EDIT DOCTOR

function editDoctor(id) {
    const doctor = doctors.find(
        item => Number(getId(item)) === Number(id)
    );

    if (!doctor) {
        showToast("Doctor not found", true);
        return;
    }

    document.getElementById("doctorId").value = getId(doctor);

    document.getElementById("doctorName").value =
        getValue(doctor, "doctorName", "DoctorName") ?? "";

    document.getElementById("doctorSpecialization").value =
        getValue(doctor, "specialization", "Specialization") ?? "";

    document.getElementById("doctorTime").value =
        getValue(doctor, "averageConsultationTime", "AverageConsultationTime") ?? "";

    document.getElementById("doctorCurrentToken").value =
        getValue(doctor, "currentServingToken", "CurrentServingToken") ?? 0;

    document.getElementById("doctorFormTitle").textContent =
        "Update Doctor";

    document.getElementById("doctorSubmitBtn").textContent =
        "Update Doctor";

    showSection("doctors", document.querySelectorAll(".tab")[2]);

    window.scrollTo({ top: 0, behavior: "smooth" });
}

// DELETE DOCTOR

async function deleteDoctor(id) {
    if (!confirm("Are you sure you want to delete this doctor?")) {
        return;
    }

    try {
        await apiRequest(
            `${API}/doctor/delete/${id}`,
            "DELETE"
        );

        showToast("Doctor deleted successfully");

        await loadDoctors();
        await loadDashboard();

    } catch (error) {
        console.error("Doctor delete error:", error);
        showToast(error.message || "Unable to delete doctor", true);
    }
}

// RESET DOCTOR FORM

function resetDoctorForm() {
    document.getElementById("doctorForm").reset();

    document.getElementById("doctorId").value = "";

    document.getElementById("doctorCurrentToken").value = 0;

    document.getElementById("doctorFormTitle").textContent =
        "Add Doctor";

    document.getElementById("doctorSubmitBtn").textContent =
        "Add Doctor";
}

// ==================================================
// TOKEN DROPDOWNS
// ==================================================

function updatePatientSelect() {
    const select = document.getElementById("tokenPatient");

    if (!select) return;

    select.innerHTML = '<option value="">Select Patient</option>';

    patients.forEach(patient => {
        const id = getId(patient);
        const name = getValue(patient, "patientName", "PatientName");

        select.innerHTML += `
            <option value="${id}">
                ${escapeHTML(name)} (ID: ${id})
            </option>
        `;
    });
}

function updateDoctorSelect() {
    const select = document.getElementById("tokenDoctor");

    if (!select) return;

    select.innerHTML = '<option value="">Select Doctor</option>';

    doctors.forEach(doctor => {
        const id = getId(doctor);
        const name = getValue(doctor, "doctorName", "DoctorName");

        select.innerHTML += `
            <option value="${id}">
                ${escapeHTML(name)} (ID: ${id})
            </option>
        `;
    });
}

// ==================================================
// TOKEN CRUD
// ==================================================

async function loadTokens() {
    try {
        const data = await apiRequest(`${API}/token/getall`);

        tokens = Array.isArray(data) ? data : [];

        const table = document.getElementById("tokenTable");

        if (table) {
            if (tokens.length === 0) {
                table.innerHTML =
                    '<tr><td colspan="9">No tokens found</td></tr>';
            } else {
                table.innerHTML = tokens.map(token => {
                    const id = getId(token);

                    const priority =
                        getValue(token, "isPriority", "IsPriority");

                    const status =
                        getValue(token, "status", "Status");

                    return `
                        <tr>
                            <td>${escapeHTML(id)}</td>
                            <td>${escapeHTML(getValue(token, "tokenNumber", "TokenNumber"))}</td>
                            <td>${escapeHTML(getPatientName(token))}</td>
                            <td>${escapeHTML(getDoctorName(token))}</td>
                            <td>${escapeHTML(getValue(token, "tokenDate", "TokenDate"))}</td>
                            <td>${priority ? "Yes" : "No"}</td>
                            <td><span class="status-badge">${escapeHTML(status)}</span></td>
                            <td>${escapeHTML(getValue(token, "estimatedWaitTime", "EstimatedWaitTime"))}</td>
                            <td>
                                <button class="action-btn edit-btn"
                                    onclick="editToken(${id})">
                                    Edit
                                </button>

                                <button class="action-btn priority-btn"
                                    onclick="makePriority(${id})">
                                    Priority
                                </button>

                                <button class="action-btn delete-btn"
                                    onclick="deleteToken(${id})">
                                    Delete
                                </button>
                            </td>
                        </tr>
                    `;
                }).join("");
            }
        }

        document.getElementById("tokenCount").textContent =
            tokens.length;

        const waitingCount = tokens.filter(token =>
            String(getValue(token, "status", "Status")).toUpperCase() === "WAITING"
        ).length;

        document.getElementById("waitingCount").textContent =
            waitingCount;

    } catch (error) {
        console.error("Token loading error:", error);
        showToast("Unable to load tokens", true);
    }
}

// GENERATE TOKEN

document.getElementById("tokenForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const patientId =
        document.getElementById("tokenPatient").value;

    const doctorId =
        document.getElementById("tokenDoctor").value;

    if (!patientId || !doctorId) {
        showToast("Please select patient and doctor", true);
        return;
    }

    try {
        await apiRequest(
            `${API}/token/generate?doctorId=${doctorId}&patientId=${patientId}`,
            "POST"
        );

        showToast("Token generated successfully");

        document.getElementById("tokenForm").reset();

        await loadTokens();
        await loadDashboard();

    } catch (error) {
        console.error("Token generation error:", error);
        showToast(error.message || "Token generation failed", true);
    }
});

// EDIT TOKEN STATUS

function editToken(id) {
    const token = tokens.find(
        item => Number(getId(item)) === Number(id)
    );

    if (!token) return;

    const currentStatus =
        getValue(token, "status", "Status") ?? "WAITING";

    const newStatus = prompt(
        "Enter status: WAITING, SERVING, COMPLETED, CANCELLED",
        currentStatus
    );

    if (newStatus === null) return;

    const allowedStatuses = [
        "WAITING",
        "SERVING",
        "COMPLETED",
        "CANCELLED"
    ];

    const status = newStatus.trim().toUpperCase();

    if (!allowedStatuses.includes(status)) {
        showToast("Invalid status", true);
        return;
    }

    updateTokenStatus(id, status);
}

async function updateTokenStatus(id, status) {
    const token = tokens.find(
        item => Number(getId(item)) === Number(id)
    );

    if (!token) return;

    const patient = getValue(token, "patient", "Patient");
    const doctor = getValue(token, "doctor", "Doctor");

    const updatedToken = {
        id: Number(id),
        tokenNumber: getValue(token, "tokenNumber", "TokenNumber"),
        tokenDate: getValue(token, "tokenDate", "TokenDate"),
        isPriority: getValue(token, "isPriority", "IsPriority"),
        status: status,
        estimatedWaitTime: getValue(token, "estimatedWaitTime", "EstimatedWaitTime"),
        patient: patient ? { id: getId(patient) } : null,
        doctor: doctor ? { id: getId(doctor) } : null
    };

    try {
        await apiRequest(
            `${API}/token/update/${id}`,
            "PUT",
            updatedToken
        );

        showToast("Token updated successfully");

        await loadTokens();
        await loadDashboard();

    } catch (error) {
        console.error("Token update error:", error);
        showToast(error.message || "Token update failed", true);
    }
}

// PRIORITY TOKEN

async function makePriority(id) {
    const token = tokens.find(
        item => Number(getId(item)) === Number(id)
    );

    if (!token) return;

    const patient = getValue(token, "patient", "Patient");
    const doctor = getValue(token, "doctor", "Doctor");

    if (!patient || !doctor) {
        showToast("Patient or doctor information missing", true);
        return;
    }

    try {
        await apiRequest(
            `${API}/token/priority?doctorId=${getId(doctor)}&patientId=${getId(patient)}`,
            "POST"
        );

        showToast("Priority request submitted");

        await loadTokens();
        await loadDashboard();

    } catch (error) {
        console.error("Priority error:", error);
        showToast(error.message || "Priority operation failed", true);
    }
}

// DELETE TOKEN

async function deleteToken(id) {
    if (!confirm("Are you sure you want to delete this token?")) {
        return;
    }

    try {
        await apiRequest(
            `${API}/token/delete/${id}`,
            "DELETE"
        );

        showToast("Token deleted successfully");

        await loadTokens();
        await loadDashboard();

    } catch (error) {
        console.error("Token delete error:", error);
        showToast(error.message || "Unable to delete token", true);
    }
}

// ==================================================
// DASHBOARD
// ==================================================

async function loadDashboard() {
    try {
        await Promise.all([
            loadPatients(),
            loadDoctors(),
            loadTokens()
        ]);

        document.getElementById("patientCount").textContent =
            patients.length;

        document.getElementById("doctorCount").textContent =
            doctors.length;

        document.getElementById("tokenCount").textContent =
            tokens.length;

        const waiting = tokens.filter(token =>
            String(getValue(token, "status", "Status")).toUpperCase() === "WAITING"
        );

        document.getElementById("waitingCount").textContent =
            waiting.length;

        const recent = [...tokens].reverse().slice(0, 5);

        const table =
            document.getElementById("dashboardTokenTable");

        if (recent.length === 0) {
            table.innerHTML =
                '<tr><td colspan="5">No tokens available</td></tr>';
        } else {
            table.innerHTML = recent.map(token => `
                <tr>
                    <td>${escapeHTML(getValue(token, "tokenNumber", "TokenNumber"))}</td>
                    <td>${escapeHTML(getPatientName(token))}</td>
                    <td>${escapeHTML(getDoctorName(token))}</td>
                    <td>${escapeHTML(getValue(token, "tokenDate", "TokenDate"))}</td>
                    <td><span class="status-badge">${escapeHTML(getValue(token, "status", "Status"))}</span></td>
                </tr>
            `).join("");
        }

    } catch (error) {
        console.error("Dashboard error:", error);
        showToast("Unable to load dashboard", true);
    }
}

async function refreshDashboard() {
    await loadDashboard();
    showToast("Dashboard refreshed");
}

// ---------------- INITIAL LOAD ----------------

document.addEventListener("DOMContentLoaded", () => {
    loadDashboard();
});