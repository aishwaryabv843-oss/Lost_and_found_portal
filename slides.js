document.addEventListener('DOMContentLoaded', () => {
    // ==========================================================================
    // 1. GLOBAL NOTIFICATIONS POPUP
    // ==========================================================================
    const alertBox = document.getElementById('portal-alert');
    function showPortalAlert(message) {
        if (!alertBox) return;
        const msgNode = alertBox.querySelector('.noti-message');
        if (msgNode) msgNode.innerText = message;
        alertBox.classList.add('show');
        setTimeout(() => {
            alertBox.classList.remove('show');
        }, 3000);
    }

    // ==========================================================================
    // 2. MOCK DATABASE STATE & LIVE WEB DEMO ENGINE WITH LOCALSTORAGE PERSISTENCE
    // ==========================================================================
    const DEFAULT_DATABASE = {
        lostItems: [
            { id: 1, title: 'Black leather Laptop Bag containing charger', category: 'bag', studentName: 'Rohan Sharma', usn: '1RV23CS084', contact: '9845012345', department: 'Computer Science', date: '2026-05-25', location: 'Main Block Seminar Hall', image: '💼', status: 'Pending', depositedTo: 'Security Room' },
            { id: 2, title: 'Casio Scientific Calculator FX-991EX', category: 'keys', studentName: 'Karthik S', usn: '1RV23EC042', contact: '9008123456', department: 'Electronics & Comm', date: '2026-05-27', location: 'Chemistry Lab Table 2', image: '🧮', status: 'Pending', depositedTo: 'Lab Incharge' },
            { id: 3, title: 'Engineering College ID Card with blue lanyard', category: 'idcard', studentName: 'Amit Hegde', usn: '1RV23EC015', contact: '9611123456', department: 'Electronics & Comm', date: '2026-05-24', location: 'Library Reading Room A', image: '🪪', status: 'Returned', depositedTo: 'College Office', returnedByAdmin: 'System Admin', returnDate: '2026-05-28' },
            { id: 4, title: 'Honda Motorcycle Keys with red tag', category: 'keys', studentName: 'Siddharth M.', usn: '1RV23ME098', contact: '9880123456', department: 'Mechanical Eng', date: '2026-05-26', location: 'Mechanical Labs parking', image: '🔑', status: 'Pending', depositedTo: 'Security Room' },
            { id: 5, title: 'MacBook Air M2 Silver 13-inch', category: 'laptop', studentName: 'Sneha Rao', usn: '1RV23CS140', contact: '9448123456', department: 'Computer Science', date: '2026-05-27', location: 'Computer Lab 3 Desk 12', image: '💻', status: 'Pending', depositedTo: 'Lab Incharge' }
        ],
        claims: [
            { claimId: 1004, itemId: 1, studentName: 'Rohan Sharma', usn: '1RV23CS084', contact: '9845012345', department: 'Computer Science', date: '2026-05-26', status: 'Pending' },
            { claimId: 1005, itemId: 3, studentName: 'Amit Hegde', usn: '1RV23EC015', contact: '9611123456', department: 'Electronics & Comm', date: '2026-05-25', status: 'Completed' }
        ]
    };

    // Load database from localStorage or set defaults
    let database = JSON.parse(localStorage.getItem('portalDatabase')) || DEFAULT_DATABASE;

    // Helper function to commit changes to localStorage
    function saveDatabaseToLocalStorage() {
        localStorage.setItem('portalDatabase', JSON.stringify(database));
    }

    let activeDemoFilter = 'all';
    let lostImageBase64 = null;
    let foundImageBase64 = null;
    let claimImageBase64 = null;

    // --- SESSION STATE INITIALIZER ---
    let activeRole = localStorage.getItem('activeSessionRole') || 'student';
    let activeUser = localStorage.getItem('activeSessionUser') || '1RV24CS015';
    let activeName = localStorage.getItem('activeSessionName') || 'Aishu R. Kumar';
    let activeDept = localStorage.getItem('activeSessionDept') || 'Computer Science';

    function initSessionView() {
        const badge = document.getElementById('app-session-badge');
        const studentNav = document.getElementById('student-sidebar-nav');
        const adminNav = document.getElementById('admin-sidebar-nav');
        
        const formName = document.getElementById('lost-form-name');
        const formUsn = document.getElementById('lost-form-usn');
        const formDept = document.getElementById('lost-form-dept');
        const finderName = document.getElementById('found-form-finder-name');
        
        if (formName) formName.value = activeName;
        if (formUsn) formUsn.value = activeUser;
        if (formDept) formDept.value = activeDept;
        if (finderName) finderName.value = activeName;

        if (activeRole === 'admin') {
            if (badge) badge.innerText = `Supervisor: ${activeName}`;
            if (studentNav) studentNav.style.display = 'none';
            if (adminNav) adminNav.style.display = 'block';
            window.switchDemoTab('admin-dashboard', document.getElementById('sidebar-dashboard-btn'));
        } else {
            if (badge) badge.innerText = `Student USN: ${activeUser}`;
            if (studentNav) studentNav.style.display = 'block';
            if (adminNav) adminNav.style.display = 'none';
            window.switchDemoTab('catalog', document.getElementById('sidebar-catalog-btn'));
        }
    }

    // --- SIGN OUT ROUTINE ---
    window.handlePortalSignOut = function() {
        localStorage.removeItem('activeSessionRole');
        localStorage.removeItem('activeSessionUser');
        localStorage.removeItem('activeSessionName');
        localStorage.removeItem('activeSessionDept');
        showPortalAlert("Session Terminated. Redirecting to login...");
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 800);
    };

    // --- RENDER FUNCTION: SEARCH CATALOG ---
    window.renderCatalog = function(query = '') {
        const grid = document.getElementById('web-catalog-grid');
        if (!grid) return;
        grid.innerHTML = '';

        const filtered = database.lostItems.filter(item => {
            const matchesCategory = (activeDemoFilter === 'all' || item.category === activeDemoFilter);
            const matchesQuery = item.title.toLowerCase().includes(query.toLowerCase()) || 
                                 item.studentName.toLowerCase().includes(query.toLowerCase()) || 
                                 item.usn.toLowerCase().includes(query.toLowerCase());
            return matchesCategory && matchesQuery;
        });

        if (filtered.length === 0) {
            grid.innerHTML = `<div style="grid-column: span 3; text-align: center; color: #64748b; padding: 3rem;">No items matching your search directory parameters.</div>`;
            return;
        }

        filtered.forEach(item => {
            const card = document.createElement('div');
            card.className = 'item-card';
            
            let imgContent = `<div style="font-size: 3rem;">${item.image}</div>`;
            if (item.image.startsWith('data:image')) {
                imgContent = `<img src="${item.image}" alt="${item.title}">`;
            }

            // Customize labels depending on whether the item is Lost or Found
            const dateLabel = (item.status === 'found' || item.status === 'Returned' || item.status === 'Ready for Collection') ? 'Date Spotted/Handed' : 'Date Lost';
            const reporterLabel = (item.status === 'found' || item.status === 'Returned' || item.status === 'Ready for Collection') ? 'Finder' : 'Reporter';
            const usnDisplay = item.usn && item.usn !== 'FINDER-STD' && item.usn !== 'ADMIN-FAC' ? ` (${item.usn})` : '';

            // Safe custody text info
            const custodyText = item.depositedTo || 'College Office';

            card.innerHTML = `
                <div class="item-card-image">
                    ${imgContent}
                    <span class="item-card-badge badge-${item.status.toLowerCase().replace(/ /g, '-')}">${item.status}</span>
                </div>
                <div class="item-card-body">
                    <div>
                        <h4 class="item-card-title">${item.title}</h4>
                        <div class="item-card-meta" style="margin-top: 10px;">
                            <div>📍 Location: ${item.location}</div>
                            <div>📅 ${dateLabel}: ${item.date}</div>
                            <div>👤 ${reporterLabel}: ${item.studentName}${usnDisplay}</div>
                            <div>🏢 Custody: <strong>${custodyText}</strong></div>
                            ${item.status === 'Returned' && item.returnedByAdmin ? `<div>🎁 Handed by: ${item.returnedByAdmin} on ${item.returnDate}</div>` : ''}
                        </div>
                    </div>
                    ${(item.status !== 'Returned' && item.status !== 'Ready for Collection' && item.status !== 'Claimed' && item.status !== 'Under Verification') ? `
                        <button class="item-card-btn" onclick="openClaimModal(${item.id})">⭐ File Ownership Claim</button>
                    ` : (item.status === 'Returned' ? `
                        <button class="item-card-btn" style="background-color: #f1f5f9; color: #94a3b8; border-color: #e2e8f0; cursor: not-allowed;" disabled>Returned & Secured</button>
                    ` : `
                        <button class="item-card-btn" style="background-color: #eff6ff; color: #1d4ed8; border-color: #bfdbfe; cursor: not-allowed;" disabled>${item.status}</button>
                    `)}
                </div>
            `;
            grid.appendChild(card);
        });
    };

    // --- DYNAMIC SEARCH & FILTER TAGS ---
    window.setDemoCategoryFilter = function(category, element) {
        document.querySelectorAll('.category-tag').forEach(t => t.classList.remove('active'));
        element.classList.add('active');
        activeDemoFilter = category;
        window.renderCatalog();
    };

    window.triggerDemoSearch = function(inputVal) {
        window.renderCatalog(inputVal);
    };

    // --- STUDENT UPLOAD: FILEREADER DATA EXTRACTION ---
    const fileInput = document.getElementById('portal-file-input');
    const uploadZone = document.getElementById('portal-upload-zone');
    
    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    lostImageBase64 = event.target.result;
                    if (uploadZone) {
                        uploadZone.innerHTML = `<img src="${lostImageBase64}" class="form-upload-preview" alt="Preview">`;
                    }
                    showPortalAlert("FileReader Success: Loaded lost image preview.");
                };
                reader.readAsDataURL(file);
            }
        });
    }

    const foundFileInput = document.getElementById('found-file-input');
    const foundUploadZone = document.getElementById('found-upload-zone');
    
    if (foundFileInput) {
        foundFileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    foundImageBase64 = event.target.result;
                    if (foundUploadZone) {
                        foundUploadZone.innerHTML = `<img src="${foundImageBase64}" class="form-upload-preview" alt="Preview">`;
                    }
                    showPortalAlert("FileReader Success: Loaded found image preview.");
                };
                reader.readAsDataURL(file);
            }
        });
    }

    window.submitPortalLostItem = function() {
        const title = document.getElementById('lost-form-title').value.trim();
        const name = document.getElementById('lost-form-name').value.trim();
        const usn = document.getElementById('lost-form-usn').value.trim();
        const contact = document.getElementById('lost-form-contact').value.trim();
        const dept = document.getElementById('lost-form-dept').value.trim();
        const loc = document.getElementById('lost-form-loc').value.trim();
        const category = document.getElementById('lost-form-category').value;

        if (!title || !name || !usn || !contact || !loc) {
            showPortalAlert("DBMS Validation Check: Please fill in all fields!");
            return;
        }

        const catIcons = { bag: '💼', phone: '📱', idcard: '🪪', keys: '🔑', laptop: '💻' };
        const newItem = {
            id: database.lostItems.length + 1,
            title: title,
            category: category,
            studentName: name,
            usn: usn,
            contact: contact,
            department: dept,
            date: new Date().toISOString().split('T')[0],
            location: loc,
            image: lostImageBase64 || catIcons[category] || '📦',
            status: 'Pending',
            depositedTo: 'College Office'
        };

        database.lostItems.unshift(newItem);
        saveDatabaseToLocalStorage(); // Commit to LocalStorage!

        showPortalAlert("JDBC Success: Record saved to Database. Status set to 'Pending'.");
        
        // Reset forms
        document.getElementById('lost-form-title').value = '';
        document.getElementById('lost-form-contact').value = '';
        document.getElementById('lost-form-loc').value = '';
        document.getElementById('lost-form-name').value = activeName;
        document.getElementById('lost-form-usn').value = activeUser;
        document.getElementById('lost-form-dept').value = activeDept;
        lostImageBase64 = null;
        if (uploadZone) {
            uploadZone.innerHTML = `
                <div class="form-upload-prompt">
                    <div style="font-size: 2rem;">📁</div>
                    <div style="font-weight: bold; margin-top: 5px;">Upload Proof Image</div>
                    <div style="font-size: 0.7rem;">Click to browse local files</div>
                </div>
            `;
        }

        window.switchDemoTab('catalog', document.getElementById('sidebar-catalog-btn'));
        window.renderCatalog();
        updateAdminStats();
    };

    // --- STATEFUL FOUND ITEM SUBMISSION (WITH FINDER DETAILS & IMAGE) ---
    window.submitPortalFoundItem = function() {
        const title = document.getElementById('found-form-title').value.trim();
        const loc = document.getElementById('found-form-loc').value.trim();
        const safe = document.getElementById('found-form-safe').value.trim();
        const finderName = document.getElementById('found-form-finder-name').value.trim();
        const finderContact = document.getElementById('found-form-finder-contact').value.trim();
        const category = document.getElementById('found-form-category').value;

        if (!title || !loc || !safe || !finderName || !finderContact) {
            showPortalAlert("Validation failed: Please fill in all fields, including Finder details.");
            return;
        }

        showPortalAlert("DBMS Success: Item registered under custody tracking!");
        
        const catIcons = { bag: '💼', phone: '📱', idcard: '🪪', keys: '🔑', laptop: '💻' };
        
        database.lostItems.unshift({
            id: database.lostItems.length + 1,
            title: `[FOUND] ${title}`,
            category: category,
            studentName: finderName,
            usn: 'FINDER-STD',
            contact: finderContact,
            department: 'Student Body',
            date: new Date().toISOString().split('T')[0],
            location: loc,
            depositedTo: safe,
            image: foundImageBase64 || catIcons[category] || '📦',
            status: 'Pending'
        });
        
        saveDatabaseToLocalStorage(); // Commit to LocalStorage!

        // Reset forms
        document.getElementById('found-form-title').value = '';
        document.getElementById('found-form-loc').value = '';
        document.getElementById('found-form-safe').value = '';
        document.getElementById('found-form-finder-contact').value = '';
        document.getElementById('found-form-category').value = 'bag';
        foundImageBase64 = null;
        if (foundUploadZone) {
            foundUploadZone.innerHTML = `
                <div class="form-upload-prompt">
                    <div style="font-size: 2rem;">📁</div>
                    <div style="font-weight: bold; margin-top: 5px;">Upload Found Image</div>
                    <div style="font-size: 0.7rem;">Click to browse local files</div>
                </div>
            `;
        }

        window.switchDemoTab('catalog', document.getElementById('sidebar-catalog-btn'));
        window.renderCatalog();
        updateAdminStats();
    };

    // --- CLAIM ACTIONS (NEW POPUP VERIFICATION & DATABASE SYNC) ---
    const claimFileInput = document.getElementById('claim-file-input');
    const claimUploadZone = document.getElementById('claim-upload-zone');
    
    if (claimFileInput) {
        claimFileInput.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(event) {
                    claimImageBase64 = event.target.result;
                    if (claimUploadZone) {
                        claimUploadZone.innerHTML = `<img src="${claimImageBase64}" class="form-upload-preview" alt="Preview">`;
                    }
                    showPortalAlert("FileReader Success: Loaded ID proof preview.");
                };
                reader.readAsDataURL(file);
            }
        });
    }

    window.openClaimModal = function(itemId) {
        const item = database.lostItems.find(i => i.id === itemId);
        if (!item) return;

        // Validation: Prevent duplicate claims
        const existing = database.claims.find(c => c.itemId === itemId && c.usn === activeUser);
        if (existing) {
            showPortalAlert("You have already submitted a claim for this item.");
            return;
        }

        // Pre-populate fields
        document.getElementById('claim-item-id').value = itemId;
        document.getElementById('claim-form-name').value = activeName;
        document.getElementById('claim-form-usn').value = activeUser;
        document.getElementById('claim-form-contact').value = '';
        document.getElementById('claim-form-dept').value = activeDept;
        document.getElementById('claim-form-proof').value = '';
        document.getElementById('claim-form-details').value = '';
        
        claimImageBase64 = null;
        if (claimUploadZone) {
            claimUploadZone.innerHTML = `
                <div id="claim-upload-prompt" class="form-upload-prompt" style="font-size: 0.75rem;">
                    <span style="font-size: 1.25rem;">📁</span>
                    <div>Click to browse supporting proof image</div>
                </div>
            `;
        }

        document.getElementById('claim-modal').style.display = 'flex';
    };

    window.closeClaimModal = function() {
        document.getElementById('claim-modal').style.display = 'none';
    };

    window.submitPortalClaimDetails = function() {
        const itemId = parseInt(document.getElementById('claim-item-id').value);
        const name = document.getElementById('claim-form-name').value.trim();
        const usn = document.getElementById('claim-form-usn').value.trim();
        const contact = document.getElementById('claim-form-contact').value.trim();
        const dept = document.getElementById('claim-form-dept').value;
        const proof = document.getElementById('claim-form-proof').value.trim();
        const details = document.getElementById('claim-form-details').value.trim();

        if (!name || !usn || !contact || !proof) {
            showPortalAlert("Validation failed: Please fill in all required fields!");
            return;
        }

        // Prevent duplicate claims
        const existing = database.claims.find(c => c.itemId === itemId && c.usn === usn);
        if (existing) {
            showPortalAlert("You have already submitted a claim for this item.");
            return;
        }

        const newClaim = {
            claimId: 1000 + database.claims.length + 1,
            itemId: itemId,
            studentName: name,
            usn: usn,
            contact: contact,
            department: dept,
            proofOfOwnership: proof,
            additionalDetails: details,
            imageProof: claimImageBase64 || '📦',
            date: new Date().toISOString().split('T')[0],
            status: 'Pending' // Pending -> Under Verification -> Ready for Collection -> Completed
        };

        const item = database.lostItems.find(i => i.id === itemId);
        if (item) {
            item.status = 'Claimed';
        }

        database.claims.unshift(newClaim);
        saveDatabaseToLocalStorage();

        showPortalAlert("Claim Submitted Successfully");
        window.closeClaimModal();
        renderClaimsTable();
        updateAdminStats();
        window.renderCatalog();
    };

    // --- ADMIN VERIFICATION CONTROLS ---
    window.startClaimVerification = function(claimId) {
        const claim = database.claims.find(c => c.claimId === claimId);
        if (claim) {
            claim.status = 'Under Verification';
            const item = database.lostItems.find(i => i.id === claim.itemId);
            if (item) item.status = 'Under Verification';
            saveDatabaseToLocalStorage();
            showPortalAlert("Claim matched. Verification started.");
            renderClaimsTable();
            updateAdminStats();
            window.renderCatalog();
        }
    };

    window.approveClaimVerification = function(claimId) {
        const claim = database.claims.find(c => c.claimId === claimId);
        if (claim) {
            const notes = prompt("Enter verification/identification notes for approval:", "Identity match verified via college ID card & photos.");
            if (notes === null) return; // User cancelled
            
            claim.status = 'Ready for Collection';
            const item = database.lostItems.find(i => i.id === claim.itemId);
            if (item) {
                item.status = 'Ready for Collection';
                item.verificationNotes = notes;
            }
            saveDatabaseToLocalStorage();
            showPortalAlert("Claim approved! Item is Ready for Collection.");
            renderClaimsTable();
            updateAdminStats();
            window.renderCatalog();
        }
    };

    window.rejectClaimVerification = function(claimId) {
        const claim = database.claims.find(c => c.claimId === claimId);
        if (claim) {
            claim.status = 'Rejected';
            const item = database.lostItems.find(i => i.id === claim.itemId);
            if (item) item.status = 'Pending'; // Revert back to Pending!
            saveDatabaseToLocalStorage();
            showPortalAlert("Claim rejected.");
            renderClaimsTable();
            updateAdminStats();
            window.renderCatalog();
        }
    };

    window.markItemAsReturned = function(claimId, itemId) {
        const claim = database.claims.find(c => c.claimId === claimId);
        const item = database.lostItems.find(i => i.id === itemId);
        if (claim && item) {
            claim.status = 'Completed';
            item.status = 'Returned';
            item.returnedByAdmin = 'System Admin';
            item.returnDate = new Date().toISOString().split('T')[0];
            
            saveDatabaseToLocalStorage();
            showPortalAlert("Success: Claim completed and item physically returned!");
            renderClaimsTable();
            window.renderCatalog();
            updateAdminStats();
        }
    };

    function renderClaimsTable() {
        const tableBody = document.getElementById('admin-claims-tbody');
        const studentTableBody = document.getElementById('student-claims-tbody');
        
        if (tableBody) {
            tableBody.innerHTML = '';
            const allClaims = database.claims;
            if (allClaims.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: #64748b;">No active verification claims.</td></tr>`;
            } else {
                allClaims.forEach(c => {
                    const item = database.lostItems.find(i => i.id === c.itemId) || { title: 'Unknown belonging', status: 'Pending' };
                    let actionHtml = '';
                    let badgeClass = 'pending';
                    
                    if (c.status === 'Pending') {
                        badgeClass = 'pending';
                        actionHtml = `
                            <button class="verify-claim-btn" style="background-color: var(--brand-600); margin-right: 5px;" onclick="startClaimVerification(${c.claimId})">Start Verification</button>
                        `;
                    } else if (c.status === 'Under Verification') {
                        badgeClass = 'found'; // Blue styling
                        actionHtml = `
                            <button class="verify-claim-btn" style="background-color: var(--accent-emerald); margin-right: 5px;" onclick="approveClaimVerification(${c.claimId})">Approve Claim</button>
                            <button class="verify-claim-btn" style="background-color: var(--accent-rose);" onclick="rejectClaimVerification(${c.claimId})">Reject</button>
                        `;
                    } else if (c.status === 'Ready for Collection') {
                        badgeClass = 'found'; // Blue/purple styling
                        actionHtml = `
                            <button class="verify-claim-btn" style="background-color: var(--brand-700);" onclick="markItemAsReturned(${c.claimId}, ${c.itemId})">Mark Returned</button>
                        `;
                    } else if (c.status === 'Rejected') {
                        badgeClass = 'pending'; // amber
                        actionHtml = `<span style="font-size:0.75rem; color:var(--accent-rose); font-weight:bold;">Claim Rejected</span>`;
                    } else if (c.status === 'Completed') {
                        badgeClass = 'returned'; // Soft green
                        actionHtml = `<span style="font-size:0.75rem; color:var(--accent-emerald); font-weight:bold;">Returned & Completed</span>`;
                    }
                    
                    tableBody.innerHTML += `
                        <tr>
                            <td>#${c.claimId}</td>
                            <td><strong>${item.title}</strong></td>
                            <td>${c.studentName}</td>
                            <td>${c.usn}</td>
                            <td>${c.contact}</td>
                            <td><span class="item-card-badge badge-${badgeClass.toLowerCase()}">${c.status}</span></td>
                            <td>${actionHtml}</td>
                        </tr>
                    `;
                });
            }
        }

        if (studentTableBody) {
            studentTableBody.innerHTML = '';
            const myClaims = database.claims.filter(c => c.usn === activeUser);
            if (myClaims.length === 0) {
                studentTableBody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: #64748b;">You have not filed any verification claims yet.</td></tr>`;
            } else {
                myClaims.forEach(c => {
                    const item = database.lostItems.find(i => i.id === c.itemId) || { title: 'Unknown belonging' };
                    let statusClass = 'pending';
                    if (c.status === 'Under Verification') statusClass = 'found';
                    if (c.status === 'Ready for Collection') statusClass = 'found';
                    if (c.status === 'Completed') statusClass = 'returned';
                    if (c.status === 'Rejected') statusClass = 'pending';
                    
                    studentTableBody.innerHTML += `
                        <tr>
                            <td>#${c.claimId}</td>
                            <td>${item.title}</td>
                            <td>${c.date}</td>
                            <td><span class="item-card-badge badge-${statusClass}">${c.status}</span></td>
                        </tr>
                    `;
                });
            }
        }
    }

    function updateAdminStats() {
        const totalLogs = document.getElementById('stat-total-logs');
        const activeClaims = document.getElementById('stat-active-claims');
        const returnedLogs = document.getElementById('stat-returned-logs');

        if (totalLogs) totalLogs.innerText = database.lostItems.length;
        if (activeClaims) activeClaims.innerText = database.claims.filter(c => c.status === 'Pending').length;
        if (returnedLogs) returnedLogs.innerText = database.lostItems.filter(i => i.status === 'returned').length;
    }

    // --- SWAP VIEWPORTS: SIDEBAR BUTTONS ---
    window.switchDemoTab = function(targetPanelId, element) {
        document.querySelectorAll('.sidebar-nav-item').forEach(btn => btn.classList.remove('active'));
        if (element) element.classList.add('active');

        document.querySelectorAll('.demo-panel').forEach(panel => {
            if (panel.id === `panel-${targetPanelId}`) {
                panel.style.display = 'block';
            } else {
                panel.style.display = 'none';
            }
        });

        if (element) {
            showPortalAlert(`App Action: Loaded ${element.innerText.trim()} panel.`);
        }
    };

    // --- INITIALIZE VIEWS ---
    initSessionView();
    window.renderCatalog();
    renderClaimsTable();
    updateAdminStats();
});
